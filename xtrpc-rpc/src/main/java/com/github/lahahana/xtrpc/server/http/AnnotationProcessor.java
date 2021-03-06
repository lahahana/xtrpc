package com.github.lahahana.xtrpc.server.http;

import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This module is created for support internal annotation process
 */
@Deprecated
public class AnnotationProcessor {

    private Map<String, Object> httpServiceContainer = new ConcurrentHashMap<String, Object>();

    public void process(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ExportAsHttpService.class)) {
            ExportAsHttpService config = clazz.getAnnotation(ExportAsHttpService.class);
            HttpHandler httpHandler = new HttpHandler() {
                @Override
                public void handleRequest(HttpServerExchange exchange) throws Exception {
                    String requestPath = exchange.getRequestPath();
                    String[] strs = requestPath.split("/");
                    String method = strs[strs.length - 1];
                    String interfaceClass = requestPath.split("/" + method)[0];
                    Object serviceRef = httpServiceContainer.get(interfaceClass);
                    if (serviceRef != null) {
                        Method targetMethod = serviceRef.getClass().getDeclaredMethod(method);
                    } else {
                        //404
                    }
                }
            };
            Undertow undertow = Undertow.builder().addHttpListener(config.port(), NetworkUtil.getLocalHostInetAddress().getHostName(), httpHandler).build();
        }
    }

    public static void main(String[] args) {
        HttpHandler httpHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(exchange.getRelativePath() + "\n");
                stringBuilder.append(exchange.getRequestPath() + "\n");
                stringBuilder.append(exchange.getRequestScheme() + "\n");
                stringBuilder.append(exchange.getRequestScheme() + "\n");
                stringBuilder.append(exchange.getRequestURI() + "\n");
                stringBuilder.append(exchange.getRequestURL() + "\n");

                exchange.getResponseSender().send(stringBuilder.toString());
            }
        };
        Undertow undertow = Undertow.builder().addHttpListener(8081, NetworkUtil.getLocalHostInetAddress().getHostName(), httpHandler).build();
        undertow.start();
    }
}
