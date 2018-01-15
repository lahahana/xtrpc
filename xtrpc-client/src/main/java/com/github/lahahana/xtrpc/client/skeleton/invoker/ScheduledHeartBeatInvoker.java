package com.github.lahahana.xtrpc.client.skeleton.invoker;

import com.github.lahahana.xtrpc.common.base.Destroyable;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ScheduledHeartBeatInvoker extends TimerTask implements Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledHeartBeatInvoker.class);

    private static final long HEART_BEAT_INTERVAL = 30000L;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomThreadFactory("heartBeatThread"));

    private final InvokerHolder invokerHolder = InvokerHolderFactory.getInstance().getInvokerHolder(Constraints.Transporter.NETTY);

    private Timer timer;

    public void start() {
        timer = new Timer("scheduledHeartBeatInvoker",true);
        timer.scheduleAtFixedRate(getSelf(), HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
        logger.debug("scheduledHeartBeatInvoker started, delay(ms)={},heart beat interval(ms)={}",HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL);
    }

    public void stop() {
        //avoid NPE when client execute requests in a short time(less than HEART_BEAT_INTERVAL) and then exit vm.
        if(timer != null)
            timer.cancel();
    }

    @Override
    public void destroy() {
        logger.info("start destroy lifecycle");
        stop();
        executorService.shutdown();
        logger.info("end destroy lifecycle");
    }

    protected abstract ScheduledHeartBeatInvoker getSelf();

    protected abstract List<HeartBeatTask> createHeartBeatTasks(List<Invoker> invokers);

    @Override
    public void run() {
        List<Invoker> invokers = invokerHolder.listAll();
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
                logger.error("exception occurs, try to unhold invoker");
                invokerHolder.unhold(invoker);
            }
        }

        protected abstract void sendHeartBeat() throws Exception;

    }
}
