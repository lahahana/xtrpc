package com.github.lahahana.xtrpc.server.stub;

import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import com.github.lahahana.xtrpc.server.handler.FunctionRequestInboundHandler;
import com.github.lahahana.xtrpc.server.handler.XTRequestInboundHandler;
import com.github.lahahana.xtrpc.server.handler.XTServerOutboundPortalHandler;
import com.github.lahahana.xtrpc.server.handler.codec.FunctionResponseEncoder;
import com.github.lahahana.xtrpc.server.handler.codec.RequestDecoder;
import com.github.lahahana.xtrpc.server.handler.codec.XTResponseEncoder;
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

    @Override
    public void bootstrap() throws StubInitializeException {
        bossEventGroup = new NioEventLoopGroup(1, new CustomThreadFactory("xtBossNettyIOThread", false));
        workerEventGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new CustomThreadFactory("xtWorkerNettyIOThread", false));

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
                                .addLast(new FunctionResponseEncoder())
                                .addLast(new XTResponseEncoder())
                                .addLast(new XTServerOutboundPortalHandler());
                    }
                });
        try {
            int inetPort = serviceConfig.getProtocol().getPort();
            serverBootstrap.bind(inetHost, inetPort).sync();
            logger.debug("service stub bind to {}:{}", inetHost, inetPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
