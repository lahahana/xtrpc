package com.github.lahahana.xtrpc.client;

import com.github.lahahana.xtrpc.client.codec.XTRequestEncoder;
import com.github.lahahana.xtrpc.client.codec.XTResponseDecoder;
import com.github.lahahana.xtrpc.client.dispatch.XTResponseDispatcher;
import com.github.lahahana.xtrpc.client.handler.XTClientInboundPortalHandler;
import com.github.lahahana.xtrpc.client.handler.XTClientOutboundPortalHandler;
import com.github.lahahana.xtrpc.client.lb.LoadBalancer;
import com.github.lahahana.xtrpc.client.lb.RandomLoadBalancer;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import com.github.lahahana.xtrpc.common.exception.InvokeTimeoutException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.Tuple;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientStub {

    private static final Logger logger = LoggerFactory.getLogger(ClientStub.class);

    private volatile static ClientStub instance;

    private ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private ServiceDiscovery serviceDiscovery = new ServiceDiscovery();

    private LoadBalancer loadBalancer = new RandomLoadBalancer();

    private XTResponseDispatcher responseDispatcher = XTResponseDispatcher.getInstance();

    private Map<String, List<EventLoopGroup>> eventLoopGroupMap = new ConcurrentHashMap<>();

    private Map<String, Tuple<Lock, Boolean>> initializationLockMap = new ConcurrentHashMap<>();//true means connection established

//    private ThreadLocal<AtomicLong> requestIdCounterTL = new ThreadLocal<AtomicLong>(){
//        @Override
//        protected AtomicLong initialValue() {
//            return new AtomicLong();
//        }
//    };

    private AtomicLong requestIdCounter = new AtomicLong();


    private ClientStub() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownAll();
        }));
    }

    public static ClientStub getInstance() {
        if (instance == null) {
            synchronized (ClientStub.class) {
                if (instance == null) {
                    instance = new ClientStub();
                }
            }
        }
        return instance;
    }

    public void start(Service service) {
        Tuple<Lock, Boolean> lockStateTuple = new Tuple<>(new ReentrantLock(), false);
        Tuple<Lock, Boolean> lockStateTuple0 = initializationLockMap.putIfAbsent(service.getUniqueKey(), lockStateTuple);
        if (lockStateTuple0 == null) {
            lockStateTuple0 = lockStateTuple;
        }
        Lock lock = lockStateTuple0.getK();
        try {
            lock.lock();
            if (lockStateTuple0.getV()) {
                logger.info("Connection for already established by others, service={} , skip event loop group bootstrap", service);
                return;
            }
            EventLoopGroup workerEventGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerEventGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF, 1024)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new XTResponseDecoder())
                                    .addLast(new XTClientInboundPortalHandler(service.getInterfaceClazz()))
                                    .addLast(new XTRequestEncoder())
                                    .addLast(new XTClientOutboundPortalHandler());
                        }
                    });
            bootstrap.connect(service.getHost(), service.getPort()).sync();

            String interfaceClazz = service.getInterfaceClazz();
            List<EventLoopGroup> eventLoopGroups = eventLoopGroupMap.get(service.getInterfaceClazz());
            if (eventLoopGroups == null) {
                eventLoopGroups = new ArrayList<>();
                eventLoopGroupMap.put(interfaceClazz, eventLoopGroups);
            }
            eventLoopGroups.add(workerEventGroup);
            lockStateTuple0.setV(true);
            logger.info("connection established: service={}", service);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceNotAvailableException(e);
        } finally {
            lock.unlock();
        }

    }

    public void shutdown(Service service) {
        if (eventLoopGroupMap != null) {
            eventLoopGroupMap.get(service.getInterfaceClazz()).stream().forEach((elg) -> elg.shutdownGracefully());
        }
        logger.info("shutdown event loop group: host={}, port={}", service.getHost(), service.getPort());
    }

    public void shutdownAll() {
        if (eventLoopGroupMap != null) {
            eventLoopGroupMap.values().stream()
                    .flatMap((x) -> x.stream()).forEach((elg) -> elg.shutdownGracefully());
        }
        logger.info("shutdown all cached event loop groups");
    }

    public Object invoke(Object obj, Method method, Object[] args) throws Exception {
        XTRequest xtRequest = buildXTRequest(method, args);

        //firstly check whether connection of service initialized or not, get channel by service from channel holder
        List<ChannelHandlerContext> channelHandlerCtxListOfInterface = channelHandlerCtxHolder.listChannelHandlerContextsOfInterface(xtRequest.getInterfaceClazz());
        if (channelHandlerCtxListOfInterface == null) {
            Service selectedService = null;
            try {
                //TO-DO get service from service discovery server: zookeeper.etcd
                List<Service> services = serviceDiscovery.listServicesByInterface(xtRequest.getInterfaceClazz());
                //TO-DO add LoadBalance solution support
                selectedService = loadBalancer.selectService(services);
                //start init connection to remote server in block way
                start(selectedService);
            } catch (ServiceNotFoundException e) {
                logger.error("service={}", selectedService.getInterfaceClazz());
            } catch (ServiceNotAvailableException e2) {
                logger.error("service={}", selectedService);
            }

            channelHandlerCtxListOfInterface = channelHandlerCtxHolder.listChannelHandlerContextsOfInterface(selectedService.getInterfaceClazz());
        }
        //TO-DO add LoadBalance solution support
        Channel channel = channelHandlerCtxListOfInterface.get(0).channel();

        //register response aware and hang rpc-call
        XTResponseAware responseAware = new XTResponseAware(xtRequest.getRequestId());
        Object lock = responseDispatcher.register(xtRequest, responseAware);
        ChannelFuture channelFuture = channel.writeAndFlush(xtRequest);
        //fail-over retry

        Object result = null;
        try {
            synchronized (lock) {
                lock.wait(xtRequest.getTimeout());
                XTResponse xtResponse = responseAware.getResponse();
                result = xtResponse.getResult();
            }
        } catch (InterruptedException e) {
            throw new InvokeTimeoutException(e);
        }

        return result;
    }

    private XTRequest buildXTRequest(Method method, Object[] args) {
        Class<?> clazz = method.getDeclaringClass();
        String interfaceName = clazz.getName();
        String methodName = method.getName();
        Class<?>[] argsType = method.getParameterTypes();

        long requestId = requestIdCounter.getAndIncrement();
        XTRequest xtRequest = new XTRequest();
        xtRequest.setRequestId(requestId);
        xtRequest.setInterfaceClazz(clazz.getName());
        xtRequest.setMethod(methodName);
        xtRequest.setArgsType(argsType);
        xtRequest.setTimeout(10000l);
        xtRequest.setArgs(args);
        return xtRequest;
    }
}
