package com.github.lahahana.xtrpc.server.dispatch;

import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.Mock;
import com.github.lahahana.xtrpc.server.ChannelHandlerCtxHolder;
import com.github.lahahana.xtrpc.server.dispatch.spi.RequestDispatcher;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class XTRequestDispatcher implements RequestDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestDispatcher.class);

    private HashMap<String, Object> mockServiceFromSpringContext = new HashMap<>();

    private ExecutorService xtRequestExecutor = Executors.newFixedThreadPool(100);

    private ChannelHandlerCtxHolder channelHandlerCtxHolder = ChannelHandlerCtxHolder.getInstance();

    public Future dispatch(Channel channel, XTRequest xtRequest) {
        String interfaceClazz = xtRequest.getInterfaceClazz();
        String method = xtRequest.getMethod();
        Class<?>[] argsType = xtRequest.getArgsType();
        Object[] args = xtRequest.getArgs();
        Object targetService = mockServiceFromSpringContext.get(interfaceClazz);
        Method targetMethod;
        Future future = null;
        XTResponse xtResponse = new XTResponse(xtRequest.getRequestId());
        try {
            targetMethod = targetService.getClass().getMethod(method, argsType);
            future = xtRequestExecutor.submit(() -> {
                            try {
                                Object result = targetMethod.invoke(targetService, args);
                                logger.info("execution result:{}", result);
                                xtResponse.setStatusCode(Constraints.STATUS_OK);
                                xtResponse.setResult(result);
                                channel.write(MessageConstraints.XTRESPONSE_HEAD);
                                channel.writeAndFlush(xtResponse);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                logger.error("service:{}.{} invocation fail", interfaceClazz, method);
                            }
                        });
        } catch (NoSuchMethodException e) {
            logger.error("service:{}.{} not found", interfaceClazz, method);
            Throwable th =  new ServiceNotFoundException("service not found", e);
            xtResponse.setStatusCode(Constraints.STATUS_ERROR);
            xtResponse.setThrowable(th);
        } catch (Exception e) {
            xtResponse.setStatusCode(Constraints.STATUS_METHOD_ERROR);
            xtResponse.setThrowable(e);
        }
        return future;
    }

    @Mock
    public void addMockService(String serviceName, Object object) {
        mockServiceFromSpringContext.put(serviceName, object);
    }
}