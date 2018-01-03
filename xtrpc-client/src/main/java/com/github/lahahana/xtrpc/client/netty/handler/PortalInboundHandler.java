package com.github.lahahana.xtrpc.client.netty.handler;

import com.github.lahahana.xtrpc.client.netty.NettyInvoker;
import com.github.lahahana.xtrpc.client.skeleton.Invoker;
import com.github.lahahana.xtrpc.client.skeleton.InvokerHolder;
import com.github.lahahana.xtrpc.client.skeleton.InvokerHolderFactory;
import com.github.lahahana.xtrpc.common.domain.Aware;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.lahahana.xtrpc.common.constant.Constraints.Transporter;

@ChannelHandler.Sharable
public class PortalInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PortalInboundHandler.class);

    private static InvokerHolder invokerHolder = InvokerHolderFactory.getInstance().getInvokerHolder(Transporter.NETTY);

    private final String interfaceClazz;

    private Invoker invoker;

    private Aware channelActiveAware;

    public PortalInboundHandler(String interfaceClazz, Aware aware) {
        this.interfaceClazz = interfaceClazz;
        this.channelActiveAware = aware;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel connected:{} ", ctx.channel());
        invoker = new NettyInvoker(interfaceClazz, ctx);
        invokerHolder.hold(invoker);
        channelActiveAware.notify(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel inactive:{}, {} ", ctx.channel(), invoker);
        invokerHolder.unhold(invoker);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregistered:{}, {} ", ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
//        invokerHolder.unholdInvoker(invoker);
    }
}