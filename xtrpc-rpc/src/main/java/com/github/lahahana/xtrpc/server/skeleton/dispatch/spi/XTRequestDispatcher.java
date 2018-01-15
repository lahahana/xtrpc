package com.github.lahahana.xtrpc.server.skeleton.dispatch.spi;

import com.github.lahahana.xtrpc.common.constant.Constraints;
import com.github.lahahana.xtrpc.common.constant.MessageConstraints;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.threadfactory.CustomThreadFactory;
import com.github.lahahana.xtrpc.common.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class XTRequestDispatcher<T> implements RequestDispatcher<T> {

    private static final Logger logger = LoggerFactory.getLogger(XTRequestDispatcher.class);

    private ExecutorService xtRequestExecutor = Executors.newFixedThreadPool(10, new CustomThreadFactory("xtWorkerThread"));

    private Object serviceRef;

    public XTRequestDispatcher(Object serviceRef) {
        this.serviceRef = serviceRef;
    }

    public Future dispatch(T pipe, XTRequest xtRequest) {
        String interfaceName = xtRequest.getInterfaceName();
        String method = xtRequest.getMethod();
        Class<?>[] argsType = xtRequest.getArgsType();
        Object[] args = xtRequest.getArgs();
        Future future = xtRequestExecutor.submit(() -> {
            XTResponse xtResponse = new XTResponse(xtRequest.getRequestId());
            try {
                Method targetMethod = serviceRef.getClass().getMethod(method, argsType);
                Object result = targetMethod.invoke(serviceRef, args);
                logger.debug("execution result:{}", result);
                xtResponse.setStatusCode(Constraints.STATUS_OK);
                xtResponse.setResult(result);

            } catch (IllegalAccessException | NoSuchMethodException e) {
                logger.error("service={}.{} not found", interfaceName, method);
                Throwable th = new ServiceNotFoundException(e);
                xtResponse.setStatusCode(Constraints.STATUS_ERROR);
                xtResponse.setThrowableClass(ServiceNotFoundException.class.getName());
                xtResponse.setThrowable(CommonUtil.getStackTraceFromThrowable(th));
            } catch (InvocationTargetException e){
                Throwable targetException = e.getTargetException();
                logger.error(targetException.getClass().getName());
                xtResponse.setStatusCode(Constraints.STATUS_METHOD_ERROR);
                xtResponse.setThrowableClass(targetException.getClass().getName());
                xtResponse.setThrowable(CommonUtil.getStackTraceFromThrowable(targetException));
            }catch (Exception e) {
                logger.error(e.getMessage(), e);
                xtResponse.setStatusCode(Constraints.STATUS_METHOD_ERROR);
                xtResponse.setThrowableClass(e.getClass().getName());
                xtResponse.setThrowable(CommonUtil.getStackTraceFromThrowable(e));
            } finally {
                try {
                    sendResponse(pipe, xtResponse);
                } catch (IOException e) {
                    logger.error("Fail to send response to client", e);
                }
            }
        });

        return future;
    }

    /**
     * Send XTResponse through specific pipeline
     * <p>
     * Sub-class need write {@link MessageConstraints#XTRESPONSE_HEAD} ahead of response
     */
    public abstract void sendResponse(T pipe, XTResponse xtResponse) throws IOException;
}