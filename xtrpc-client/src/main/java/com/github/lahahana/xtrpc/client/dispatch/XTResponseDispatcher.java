package com.github.lahahana.xtrpc.client.dispatch;

import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;
import com.github.lahahana.xtrpc.common.domain.XTResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatch response to specific receiver
 * */
public final class XTResponseDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(XTResponseDispatcher.class);

    private static volatile XTResponseDispatcher instance;

    private Map<Long, XTResponseAware> registeredXTResponseAwareMap = new ConcurrentHashMap<>();

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

    public XTResponseAware register(XTRequest request) {
        XTResponseAware xtResponseAware = new XTResponseAware(request.getRequestId());
        registeredXTResponseAwareMap.put(request.getRequestId(), xtResponseAware);
        return xtResponseAware;
    }

    public void dispatch(XTResponse response) {
        logger.debug("dispatch XTResponse: requestId={}", response.getRequestId());
        XTResponseAware responseAware = registeredXTResponseAwareMap.remove(response.getRequestId());
        responseAware.notify(response);
    }

}
