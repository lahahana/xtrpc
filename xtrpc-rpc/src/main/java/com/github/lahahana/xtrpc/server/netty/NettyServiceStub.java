package com.github.lahahana.xtrpc.server.netty;

import com.github.lahahana.xtrpc.common.codec.CodecUtil;
import com.github.lahahana.xtrpc.common.codec.CodecUtilFactory;
import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceRegisterException;
import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.server.netty.handler.FunctionRequestInboundHandler;
import com.github.lahahana.xtrpc.server.netty.handler.XTRequestInboundHandler;
import com.github.lahahana.xtrpc.server.netty.handler.XTServerOutboundPortalHandler;
import com.github.lahahana.xtrpc.server.netty.handler.codec.FunctionResponseEncoder;
import com.github.lahahana.xtrpc.server.netty.handler.codec.RequestDecoder;
import com.github.lahahana.xtrpc.server.netty.handler.codec.XTResponseEncoder;
import com.github.lahahana.xtrpc.server.skeleton.registry.ServiceRegistry;
import com.github.lahahana.xtrpc.server.skeleton.registry.ServiceRegistryFactory;
import com.github.lahahana.xtrpc.server.skeleton.stub.AbstractServiceStub;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServiceStub extends AbstractServiceStub {

    private static final Logger logger = LoggerFactory.getLogger(NettyServiceStub.class);

    private EventLoopGroup bossEventGroup;

    private EventLoopGroup workerEventGroup;

    public NettyServiceStub(ServiceConfig serviceConfig) {
        super(serviceConfig);
    }

    /**
     * Listen on specific port to receive request, and try to register service to remote registry if need,
     *
     * @Throws {@linkplain StubInitializeException}
     */
    @Override
    public void bootstrap() throws StubInitializeException {
        bossEventGroup = new NioEventLoopGroup(1, new CustomThreadFactory("xtBossNettyIOThread", false));
        workerEventGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new CustomThreadFactory("xtWorkerNettyIOThread", false));
        final CodecUtil codecUtil = CodecUtilFactory.getCodecUtil(serviceConfig.getProtocol().getSerialization());
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventGroup, workerEventGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_RCVBUF, 1024)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new RequestDecoder())
                                .addLast(new XTRequestInboundHandler(getServiceRef()))
                                .addLast(new FunctionRequestInboundHandler())
                                .addLast(new FunctionResponseEncoder(codecUtil))
                                .addLast(new XTResponseEncoder(codecUtil))
                                .addLast(new XTServerOutboundPortalHandler());
                    }
                });
        try {
            int inetPort = serviceConfig.getProtocol().getPort();
            serverBootstrap.bind(inetHost, inetPort).sync();
            logger.debug("service stub bind to {}:{}", inetHost, inetPort);

            boolean needRegistry = serviceConfig.getRegistry() == null ? false : true;
            if (needRegistry) {
                ServiceRegistry serviceRegistry = ServiceRegistryFactory.getServiceRegistry(serviceConfig.getRegistry());
                Service service = new Service(serviceConfig.getInterfaceClass().getName(), serviceConfig.getProtocol().getName(), NetworkUtil.getLocalHostInetAddress().getHostName(), serviceConfig.getProtocol().getPort());
                serviceRegistry.register(service);
            }
        } catch (ServiceRegisterException e) {
            logger.error("fail to register service: serviceConfig={}", serviceConfig, e);
        } catch (Exception e) {
            shutdown();
            throw new StubInitializeException("service stub fail to initialize", e);
        }
    }

    @Override
    public void shutdown() {
        bossEventGroup.shutdownGracefully();
        workerEventGroup.shutdownGracefully();
    }

}
