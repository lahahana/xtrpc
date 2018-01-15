package com.github.lahahana.xtrpc.client.skeleton.dispatch;

import com.github.lahahana.xtrpc.common.domain.XTResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//TO-DO
public class ServiceGroupXTResponseDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(ServiceGroupXTResponseDispatcher.class);

    private Map<String, XTResponseDispatcher> dispatcherMap = new ConcurrentHashMap();

    public void dispatch(XTResponse response) {
    }
}
