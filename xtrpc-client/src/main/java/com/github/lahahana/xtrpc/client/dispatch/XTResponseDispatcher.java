package com.github.lahahana.xtrpc.client.dispatch;

import com.github.lahahana.xtrpc.client.ClientStub;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatch response to specific receiver
 * */
public class XTResponseDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(XTResponseDispatcher.class);

    private static volatile XTResponseDispatcher instance;

    private Map<Long, XTResponseAware> registeredXTResponseAware = new ConcurrentHashMap<>();

    private ThreadLocal<Map<Long, XTResponseAware>> registeredXTResponseAwareTL = new XTResponseDispatcherThreadLocal();

    private XTResponseDispatcher() {
        super();
    }

    public static XTResponseDispatcher getInstance() {
        if(instance == null) {
            synchronized (XTResponseDispatcher.class) {
                if(instance == null) {
                    instance = new XTResponseDispatcher();
                }
            }
        }
        return instance;
    }

    public Object register(XTRequest request, XTResponseAware responseAware) {
        registeredXTResponseAware.put(request.getRequestId(), responseAware);
        return responseAware;
    }

    public void dispatch(XTResponse response) {
        XTResponseAware responseAware = registeredXTResponseAware.remove(response.getRequestId());
        responseAware.setResponse(response);
        synchronized (responseAware) {
            responseAware.notify();
        }
        logger.debug("dispatch XTResponse: requestId={}", response.getRequestId());
    }

    @Deprecated
    private class XTResponseDispatcherThreadLocal extends ThreadLocal{
        @Override
        protected Object initialValue() {
            return new HashMap<>();
        }
    }
}
