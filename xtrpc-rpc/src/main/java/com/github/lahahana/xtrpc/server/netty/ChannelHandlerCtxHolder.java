package com.github.lahahana.xtrpc.server.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelHandlerCtxHolder {

    private static volatile ChannelHandlerCtxHolder instance;

    private ConcurrentHashMap<String, ChannelHandlerContext> channelCtxCache = new ConcurrentHashMap();

    private ChannelHandlerCtxHolder() {
    }

    public static ChannelHandlerCtxHolder getInstance() {
        if (instance == null) {
            synchronized (ChannelHandlerCtxHolder.class) {
                if (instance == null) {
                    instance = new ChannelHandlerCtxHolder();
                }
            }
        }
        return instance;
    }

    public void registerChannel(ChannelHandlerContext ctx) {
        channelCtxCache.put(ctx.channel().id().asLongText(), ctx);
    }

    public void removeChannelHandlerCtx(ChannelHandlerContext ctx) {
        channelCtxCache.remove(ctx.channel().id().asLongText());
    }

    public ChannelHandlerContext getChannelHandlerCtx(String channelId) {
        return channelCtxCache.get(channelId);
    }
}
