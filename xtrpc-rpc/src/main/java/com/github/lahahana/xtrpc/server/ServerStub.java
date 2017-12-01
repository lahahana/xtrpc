package com.github.lahahana.xtrpc.server;

import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import com.github.lahahana.xtrpc.common.util.Mock;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.server.handler.codec.XTRequestDecoder;
import com.github.lahahana.xtrpc.server.handler.codec.XTResponseEncoder;
import com.github.lahahana.xtrpc.server.handler.XTServerInboundPortalHandler;
import com.github.lahahana.xtrpc.server.handler.XTServerOutboundPortalHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStub {

    private static final Logger logger = LoggerFactory.getLogger(ServerStub.class);

    private EventLoopGroup bossEventGroup;

    private EventLoopGroup workerEventGroup;

    private String inetHost = NetworkUtil.getLocalHostInetAddress().getHostAddress();

    private int inetPort = 8088;

    XTServerInboundPortalHandler xtServerInboundPortalHandler = new XTServerInboundPortalHandler();

    public ServerStub() {
    }

    public ServerStub(String inetHost, int inetPort) {
        this.inetHost = inetHost;
        this.inetPort = inetPort;
    }

    public void start() throws StubInitializeException {
        bossEventGroup = new NioEventLoopGroup(1, new CustomThreadFactory("bossNettyIOThread"));
        workerEventGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new CustomThreadFactory("workerNettyIOThread"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventGroup, workerEventGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .option(ChannelOption.SO_RCVBUF, 1024)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new XTRequestDecoder())
                                .addLast(xtServerInboundPortalHandler)
                                .addLast(new XTServerOutboundPortalHandler())
                                .addLast(new XTResponseEncoder());
                    }
                });
        try {
            ChannelFuture bindFuture = serverBootstrap.bind(inetHost, inetPort).sync();
            logger.debug("server stub bind to {}:{}", inetHost, inetPort);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new StubInitializeException("server stub fail to initialize", e);
        }
    }


    public void close() {
        bossEventGroup.shutdownGracefully();
        workerEventGroup.shutdownGracefully();
    }

    @Mock
    public void addMockService(String serviceName, Object object) {
        xtServerInboundPortalHandler.addMockService(serviceName, object);
    }
}
