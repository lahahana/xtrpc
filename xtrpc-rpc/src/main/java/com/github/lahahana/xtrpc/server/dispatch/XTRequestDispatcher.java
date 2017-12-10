package com.github.lahahana.xtrpc.server.dispatch;

import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import com.github.lahahana.xtrpc.server.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.server.dispatch.spi.RequestDispatcher;
import com.github.lahahana.xtrpc.server.stub.ServiceHolder;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class XTRequestDispatcher implements RequestDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestDispatcher.class);

    private ExecutorService xtRequestExecutor = Executors.newFixedThreadPool(10, new CustomThreadFactory("xtWorkerThread"));

    private ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    private Object serviceRef;

    public XTRequestDispatcher(Object serviceRef) {
        this.serviceRef = serviceRef;
    }


    public Future dispatch(Channel channel, XTRequest xtRequest) {
        String interfaceName = xtRequest.getInterfaceName();
        String method = xtRequest.getMethod();
        Class<?>[] argsType = xtRequest.getArgsType();
        Object[] args = xtRequest.getArgs();
        Future future = xtRequestExecutor.submit(() -> {
            XTResponse xtResponse = new XTResponse(xtRequest.getRequestId());
            try {
                Method targetMethod = serviceRef.getClass().getMethod(method, argsType);
                Object result = targetMethod.invoke(serviceRef, args);
                logger.info("execution result:{}", result);
                xtResponse.setStatusCode(Constraints.STATUS_OK);
                xtResponse.setResult(result);

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("service={}.{} not found", interfaceName, method);
                Throwable th = new ServiceNotFoundException();
                xtResponse.setStatusCode(Constraints.STATUS_ERROR);
                xtResponse.setThrowable(th.toString());
            } catch (Exception e) {
                xtResponse.setStatusCode(Constraints.STATUS_METHOD_ERROR);
                xtResponse.setThrowable(e.toString());
            } finally {
                channel.write(MessageConstraints.XTRESPONSE_HEAD);
                channel.writeAndFlush(xtResponse);
            }
        });

        return future;
    }
}