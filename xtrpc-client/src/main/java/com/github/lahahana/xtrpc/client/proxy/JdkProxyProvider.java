package com.github.lahahana.xtrpc.client.proxy;

import com.github.lahahana.xtrpc.client.ClientStub;
import com.github.lahahana.xtrpc.common.exception.InvokeTimeoutException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.access.InvocationFailureException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyProvider implements ProxyProvider {

    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new JdkProxy());
    }

    private static class JdkProxy implements InvocationHandler {

        private static final Logger logger = LoggerFactory.getLogger(JdkProxy.class);

        private static ClientStub clientStub = ClientStub.getInstance();

        private JdkProxy() {}

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            logger.debug("service:{}, method:{}, args:{}",proxy.getClass().getName(), method.getName(), args);
            return clientStub.invoke(proxy, method, args);
        }

    }
}
