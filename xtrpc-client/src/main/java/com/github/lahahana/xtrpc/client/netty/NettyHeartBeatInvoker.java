package com.github.lahahana.xtrpc.client.netty;

import com.github.lahahana.xtrpc.client.skeleton.Invoker;
import com.github.lahahana.xtrpc.client.skeleton.ScheduledHeartBeatInvoker;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

//TO-DO add heartbeat task pool support
public final class NettyHeartBeatInvoker extends ScheduledHeartBeatInvoker {

    private final Logger logger = LoggerFactory.getLogger(NettyHeartBeatInvoker.class);

    @Override
    protected ScheduledHeartBeatInvoker getSelf() {
        return new NettyHeartBeatInvoker();
    }

    @Override
    protected List<HeartBeatTask> createHeartBeatTasks(List<Invoker> invokers) {
        return invokers.stream().map((invoker) -> {
            return new NettyHeartBeatTask((NettyInvoker) invoker);
        }).collect(Collectors.toList());

    }

    protected final class NettyHeartBeatTask extends HeartBeatTask<NettyInvoker> {

        public NettyHeartBeatTask(NettyInvoker invoker) {
            this.invoker = invoker;
        }

        @Override
        public void run() {
            sendHeartBeat();
        }

        protected void sendHeartBeat() {
            Channel channel = invoker.channel();
            logger.debug("send heart beat on channel: {}", channel);
            if (channel.isWritable()) {
                FunctionRequest functionRequest = new FunctionRequest(MessageConstraints.FUNCTION_REQUEST_HEAD);
                ChannelFuture future = channel.writeAndFlush(functionRequest);
                InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
                future.syncUninterruptibly();
            }
        }
    }
}
