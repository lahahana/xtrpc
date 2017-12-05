package com.github.lahahana.xtrpc.client;

import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduledHeartBeatInvoker extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledHeartBeatInvoker.class);

    private static final long HEART_BEAT_INTERVAL = 30000L;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomThreadFactory("HeartBeatThread"));

    private final ChannelHandlerCtxHolder ctxHolder = ChannelHandlerCtxHolder.getInstance();

    private Timer timer;

    public void start() {
        timer = new Timer("ScheduledHeartBeatInvoker",true);
        timer.scheduleAtFixedRate(new ScheduledHeartBeatInvoker(), HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
        logger.debug("ScheduledHeartBeatInvoker started, heart beat interval(ms):{}", HEART_BEAT_INTERVAL);
    }

    public void stop() {
        timer.cancel();
    }

    @Override
    public void run() {
        List<ChannelHandlerContext> contexts = ctxHolder.listChannelHandlerContexts();
        logger.debug("heart beat tasks scheduled, size:{}", contexts.size());
        contexts.stream().forEach((ctx) -> {
                executorService.submit(new HeartBeatTask(ctx.channel()));
            });
        }

    private static class HeartBeatTask implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);
        private Channel channel;

        public HeartBeatTask(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            sendHeartBeat();
        }

        private void sendHeartBeat() {
            logger.debug("send heart beat on channel: {}", channel);
            if(channel.isWritable()) {
                FunctionRequest functionRequest = new FunctionRequest(MessageConstraints.FUNCTION_REQUEST_HEAD);
                ChannelFuture future = channel.writeAndFlush(functionRequest);
                future.syncUninterruptibly();
            }
        }
    }
}
