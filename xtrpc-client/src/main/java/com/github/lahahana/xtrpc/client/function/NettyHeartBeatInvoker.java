package com.github.lahahana.xtrpc.client.function;

import com.github.lahahana.xtrpc.client.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public final class NettyHeartBeatInvoker extends ScheduledHeartBeatInvoker {

    private final ChannelHandlerCtxHolder ctxHolder = ChannelHandlerCtxHolder.getInstance();

    @Override
    protected ScheduledHeartBeatInvoker getSelf() {
        return new NettyHeartBeatInvoker();
    }

    @Override
    protected List<HeartBeatTask> getHeartBeatTasks() {
        return ctxHolder.listChannelHandlerContexts()
                        .stream()
                        .map((ctx) -> new NettyHeartBeatTask(ctx.channel()))
                        .collect(Collectors.toList());
    }

    protected static final class NettyHeartBeatTask extends HeartBeatTask {
        private static final Logger logger = LoggerFactory.getLogger(ScheduledHeartBeatInvoker.HeartBeatTask.class);

        private Channel channel;

        public NettyHeartBeatTask(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            sendHeartBeat();
        }

        protected void sendHeartBeat() {
            logger.debug("send heart beat on channel: {}", channel);
            if(channel.isWritable()) {
                FunctionRequest functionRequest = new FunctionRequest(MessageConstraints.FUNCTION_REQUEST_HEAD);
                ChannelFuture future = channel.writeAndFlush(functionRequest);
                future.syncUninterruptibly();
            }
        }
    }
}
