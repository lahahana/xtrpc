package com.github.lahahana.xtrpc.client;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChannelHandlerCtxHolder {

    private static volatile ChannelHandlerCtxHolder instance;

    private ConcurrentHashMap<String, List<ChannelHandlerContext>> channelHandlerCtxCache = new ConcurrentHashMap();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

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

    public void registerChannelHandlerContext(String interfaceClazz, ChannelHandlerContext ctx) {
        try {
            writeLock.lock();
            List<ChannelHandlerContext> channelsOfInterface = channelHandlerCtxCache.get(interfaceClazz);
            if(channelsOfInterface == null) {
                channelsOfInterface = new ArrayList();
                channelHandlerCtxCache.put(interfaceClazz, channelsOfInterface);
            }
            channelsOfInterface.add(ctx);
        } finally {
            writeLock.unlock();
        }
    }

    public List<ChannelHandlerContext> listChannelHandlerContextsOfInterface(String interfaceClazz) {
        try {
            readLock.lock();
            return channelHandlerCtxCache.get(interfaceClazz);
        } finally {
            readLock.unlock();
        }
    }

    public void removeChannelHandlerCtxOfInterface(String interfaceClazz) {
        channelHandlerCtxCache.remove(interfaceClazz);
    }

    public void removeChannelHandlerContext(String interfaceClazz, ChannelHandlerContext ctx) {
        try {
            writeLock.lock();
            List<ChannelHandlerContext> contexts;
            contexts = channelHandlerCtxCache.get(interfaceClazz);
            Iterator<ChannelHandlerContext> iter = contexts.iterator();

            for (;iter.hasNext();) {
                ChannelHandlerContext octx = iter.next();
                if(octx.channel().id().equals(octx.channel().id())) {
                    iter.remove();
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

}
