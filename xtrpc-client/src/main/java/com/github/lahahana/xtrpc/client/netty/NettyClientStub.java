package com.github.lahahana.xtrpc.client.netty;

import com.github.lahahana.xtrpc.client.netty.handler.FunctionResponseInboundHandler;
import com.github.lahahana.xtrpc.client.netty.handler.PortalInboundHandler;
import com.github.lahahana.xtrpc.client.netty.handler.XTClientOutboundPortalHandler;
import com.github.lahahana.xtrpc.client.netty.handler.XTResponseInboundHandler;
import com.github.lahahana.xtrpc.client.netty.handler.codec.FunctionRequestEncoder;
import com.github.lahahana.xtrpc.client.netty.handler.codec.ResponseDecoder;
import com.github.lahahana.xtrpc.client.netty.handler.codec.XTRequestEncoder;
import com.github.lahahana.xtrpc.client.skeleton.invoker.InvokerHolder;
import com.github.lahahana.xtrpc.client.skeleton.invoker.ScheduledHeartBeatInvoker;
import com.github.lahahana.xtrpc.client.skeleton.stub.ClientStub;
import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.domain.Aware;
import com.github.lahahana.xtrpc.common.domain.ChannelActiveAware;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.TimeoutException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NettyClientStub extends ClientStub {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientStub.class);

    private static final String xtServiceThreadPrefix = "xtWorkerNettyIOThread";

    private volatile static NettyClientStub instance;

    private Map<String, List<EventLoopGroup>> eventLoopGroupsMap = new ConcurrentHashMap<>();

    private Object lock = new Object();

    private NettyClientStub() {
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
    protected void initRefService0(Service service) throws ServiceNotAvailableException {
        final CodecUtil codecUtil = CodecUtilFactory.getCodecUtil(service.getProtocol());
        final Aware aware = new ChannelActiveAware();
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
                                .addLast(new PortalInboundHandler(service.getServiceInterface(), aware))
                                .addLast(new FunctionResponseInboundHandler())
                                .addLast(new FunctionRequestEncoder(codecUtil))
                                .addLast(new XTRequestEncoder(codecUtil))
                                .addLast(new XTClientOutboundPortalHandler(service.getServiceInterface()))
                        ;
                    }
                });
        try {
            bootstrap.connect(service.getHost(), service.getPort()).sync();
        } catch (InterruptedException e) {
            String errMsg = "fail to init ref service:" + service.toString();
            logger.error(errMsg, e);
            throw new ServiceNotAvailableException(errMsg, e);
        }

        String interfaceClazz = service.getServiceInterface();

        //TO-DO optimization
        synchronized (lock) {
            List<EventLoopGroup> eventLoopGroups = eventLoopGroupsMap.get(service.getServiceInterface());
            if (eventLoopGroups == null) {
                eventLoopGroups = new ArrayList<>();
                eventLoopGroupsMap.put(interfaceClazz, eventLoopGroups);
            }
            eventLoopGroups.add(workerEventGroup);
        }
        //block until channel active
        try {
            aware.aware();
        } catch (TimeoutException e) {
            throw new ServiceNotAvailableException(e);
        }
    }

    @Override
    protected InvokerHolder getInvokerHolder() {
        return invokerHolderFactory.getInvokerHolder(Constraints.Transporter.NETTY);
    }

    @Override
    protected ScheduledHeartBeatInvoker getScheduledHeartBeatInvoker() {
        return new NettyHeartBeatInvoker();
    }

    @Override
    public void shutdownRefService(Service service) {
        if (eventLoopGroupsMap != null) {
            eventLoopGroupsMap.get(service.getServiceInterface()).stream().forEach((elg) -> elg.shutdownGracefully());
        }
        logger.debug("shutdown event loop group of ref service={}", service);
    }

    @Override
    public void shutdown() {
        online = false;
        if (eventLoopGroupsMap != null) {
            eventLoopGroupsMap.values().stream()
                    .flatMap((x) -> x.stream()).forEach((elg) -> elg.shutdownGracefully());
        }
        logger.debug("shutdown all cached event loop groups");
    }

}
