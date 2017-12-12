package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.client.netty.NettyClientStub;
import com.github.lahahana.xtrpc.common.base.SingletonDestroyableFactory;
import com.github.lahahana.xtrpc.common.config.api.Protocol;

import java.util.HashSet;
import java.util.Set;

public class ClientStubFactory extends SingletonDestroyableFactory {

    private static volatile ClientStubFactory instance;

    private Set<ClientStub> clientStubs = new HashSet<>();

    public static ClientStubFactory getInstance() {
        if (instance == null) {
            synchronized (ClientStubFactory.class) {
                if (instance == null) {
                    instance = new ClientStubFactory();
                }
            }
        }
        return instance;
    }

    public synchronized ClientStub getClientStub(Protocol protocol) {
        String transporter = protocol.getTransporter();
        ClientStub stub = null;
        switch (transporter) {
            case "netty":
                stub = NettyClientStub.getInstance();
                clientStubs.add(stub);
                break;
            default:
                throw new IllegalArgumentException("unknown transporter type" + transporter);
        }

        return stub;
    }

    @Override
    public void destroy() {
        clientStubs.parallelStream().forEach((stub) -> stub.destroy());
    }
}
