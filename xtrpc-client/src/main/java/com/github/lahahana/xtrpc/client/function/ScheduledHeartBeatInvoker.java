package com.github.lahahana.xtrpc.client.function;

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

    private final ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomThreadFactory("HeartBeatThread"));

    private Timer timer;

    public void start() {
        timer = new Timer("ScheduledHeartBeatInvoker",true);
        timer.scheduleAtFixedRate(getSelf(), HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
        logger.debug("ScheduledHeartBeatInvoker started, heart beat interval(ms):{}", HEART_BEAT_INTERVAL);
    }

    public void stop() {
        timer.cancel();
    }

    protected abstract ScheduledHeartBeatInvoker getSelf();

    protected abstract List<HeartBeatTask> getHeartBeatTasks();

    @Override
    public void run() {
        List<HeartBeatTask> tasks = getHeartBeatTasks();
        logger.debug("heart beat tasks scheduled, size:{}", tasks.size());
        tasks.stream().forEach((task) -> {
                executorService.submit(task);
            });
        }

    static abstract class HeartBeatTask implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);

        public HeartBeatTask() {
        }

        @Override
        public void run() {
            sendHeartBeat();
        }

        protected abstract void sendHeartBeat();
    }
}
