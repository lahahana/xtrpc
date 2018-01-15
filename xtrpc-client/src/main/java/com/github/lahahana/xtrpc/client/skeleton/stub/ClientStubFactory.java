package com.github.lahahana.xtrpc.client.skeleton.stub;

import com.github.lahahana.xtrpc.client.netty.NettyClientStub;
import com.github.lahahana.xtrpc.common.base.SingletonDestroyableFactory;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.constant.Constraints.Transporter;

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
        Transporter transporter = Transporter.valueOf(protocol.getTransporter());
        ClientStub stub = null;
        switch (transporter) {
            case NETTY:
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
