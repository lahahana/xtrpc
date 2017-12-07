package com.github.lahahana.xtrpc.client.stub;

import com.github.lahahana.xtrpc.client.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.client.function.NettyHeartBeatInvoker;
import com.github.lahahana.xtrpc.client.function.ScheduledHeartBeatInvoker;
import com.github.lahahana.xtrpc.client.handler.FunctionResponseInboundHandler;
import com.github.lahahana.xtrpc.client.handler.XTClientOutboundPortalHandler;
import com.github.lahahana.xtrpc.client.handler.XTResponseInboundHandler;
import com.github.lahahana.xtrpc.client.handler.codec.FunctionRequestEncoder;
import com.github.lahahana.xtrpc.client.handler.codec.ResponseDecoder;
import com.github.lahahana.xtrpc.client.handler.codec.XTRequestEncoder;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import com.github.lahahana.xtrpc.common.exception.InvokeTimeoutException;
import com.github.lahahana.xtrpc.common.exception.RejectInvokeException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
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

public final class NettyClientStub extends ClientStub {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientStub.class);

    private static final String xtServiceThreadPrefix = "xtWorkerNettyIOThread";

    private volatile static NettyClientStub instance;

    private Map<String, List<EventLoopGroup>> eventLoopGroupMap = new ConcurrentHashMap<>();

    private ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private ScheduledHeartBeatInvoker scheduledHeartBeatInvoker = new NettyHeartBeatInvoker();


    private NettyClientStub() {
        scheduledHeartBeatInvoker.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));
    }

    public static NettyClientStub getInstance() {
        if (instance == null) {
            synchronized (NettyClientStub.class) {
                if (instance == null) {
                    instance = new NettyClientStub();
                }
            }
        }
        return instance;
    }

    @Override
    protected void initRefService0(Service service) throws Exception {
        String threadFactoryName = xtServiceThreadPrefix + "-" + service.getUniqueKey();
        EventLoopGroup workerEventGroup = new NioEventLoopGroup(5, new CustomThreadFactory(threadFactoryName));
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
                        ch.pipeline().addLast(new ResponseDecoder())
                                .addLast(new XTResponseInboundHandler(service.getServiceInterface()))
                                .addLast(new FunctionResponseInboundHandler())
                                .addLast(new FunctionRequestEncoder())
                                .addLast(new XTRequestEncoder())
                                .addLast(new XTClientOutboundPortalHandler(service.getServiceInterface()))
                        ;
                    }
                });
        bootstrap.connect(service.getHost(), service.getPort()).sync();

        String interfaceClazz = service.getServiceInterface();
        List<EventLoopGroup> eventLoopGroups = eventLoopGroupMap.get(service.getServiceInterface());
        if (eventLoopGroups == null) {
            eventLoopGroups = new ArrayList<>();
            eventLoopGroupMap.put(interfaceClazz, eventLoopGroups);
        }
        eventLoopGroups.add(workerEventGroup);
    }

    @Override
    public void shutdownRefService(Service service) {
        if (eventLoopGroupMap != null) {
            eventLoopGroupMap.get(service.getServiceInterface()).stream().forEach((elg) -> elg.shutdownGracefully());
        }
        logger.debug("shutdown event loop group of ref service={}", service);
    }

    @Override
    public void shutdown() {
        online = false;
        if (eventLoopGroupMap != null) {
            eventLoopGroupMap.values().stream()
                    .flatMap((x) -> x.stream()).forEach((elg) -> elg.shutdownGracefully());
        }
        logger.debug("shutdown all cached event loop groups");
    }

    protected Service selectService(String interfaceName) {
        return null;
    }

    @Override
    protected void sendXTRequest(Service service, XTRequest xtRequest) {
        //firstly check whether connection of service initialized or not, get channel by service from channel holder
        List<ChannelHandlerContext> channelHandlerCtxListOfInterface = channelHandlerCtxHolder.listChannelHandlerContextsOfInterface(xtRequest.getInterfaceName());
        if (channelHandlerCtxListOfInterface == null) {
            Service selectedService = null;
            try {
                //start init connection to remote server in block way
                initRefService(selectedService);
            } catch (ServiceNotAvailableException e2) {
                logger.error("service={}", selectedService);
                throw e2;
            }

            channelHandlerCtxListOfInterface = channelHandlerCtxHolder.listChannelHandlerContextsOfInterface(selectedService.getServiceInterface());
        }
        Channel channel = channelHandlerCtxListOfInterface.get(0).channel();
        ChannelFuture channelFuture = channel.writeAndFlush(xtRequest);
    }
}
