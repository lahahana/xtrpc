package com.github.lahahana.xtrpc.client.skeleton.proxy;

import com.github.lahahana.xtrpc.client.skeleton.stub.ClientStub;
import com.github.lahahana.xtrpc.client.skeleton.stub.ClientStubFactory;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyProvider implements ProxyProvider {

    @Override
    public <T> T getProxy(Class<T> clazz, Protocol protocol) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new JdkProxy(protocol));
    }

    private static class JdkProxy implements InvocationHandler {

        private static final Logger logger = LoggerFactory.getLogger(JdkProxy.class);

        private final Protocol protocol;

        private final ClientStub clientStub;

        private JdkProxy(Protocol protocol) {
            this.protocol = protocol;
            this.clientStub = ClientStubFactory.getInstance().getClientStub(protocol);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            logger.debug("service:{}, method:{}, args:{}", method.getDeclaringClass().getName(), method.getName(), args);
            return clientStub.invoke(method, args, protocol);
        }

    }
}
