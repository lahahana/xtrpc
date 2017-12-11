package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ScheduledHeartBeatInvoker extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledHeartBeatInvoker.class);

    private static final long HEART_BEAT_INTERVAL = 30000L;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomThreadFactory("heartBeatThread"));

    private final ServiceHolder serviceHolder = ServiceHolder.getInstance();

    private final InvokerHolder invokerHolder = InvokerHolderFactory.getInvokerHolder();

    private Timer timer;

    public void start() {
        timer = new Timer("scheduledHeartBeatInvoker",true);
        timer.scheduleAtFixedRate(getSelf(), HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
        logger.debug("scheduledHeartBeatInvoker started, delay(ms)={},heart beat interval(ms)={}",HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
    }

    public void stop() {
        timer.cancel();
    }

    protected abstract ScheduledHeartBeatInvoker getSelf();

    protected abstract List<HeartBeatTask> createHeartBeatTasks(List<Invoker> invokers);

    @Override
    public void run() {
        List<Invoker> invokers = invokerHolder.listInvokers();
        List<HeartBeatTask> tasks = createHeartBeatTasks(invokers);
        logger.debug("heart beat tasks scheduled, size:{}", tasks.size());
        tasks.stream().forEach((task) -> {
                executorService.submit(task);
            });
        }

    protected abstract class HeartBeatTask<T extends Invoker> implements Runnable {

        protected T invoker;

        public HeartBeatTask() {
        }

        @Override
        public void run() {
            try {
                sendHeartBeat();
            } catch (Exception e) {
                invokerHolder.unholdInvoker(invoker);
            }
        }

        protected abstract void sendHeartBeat() throws Exception;

    }
}
