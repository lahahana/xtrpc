package com.github.lahahana.xtrpc.client.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
@Setter
public class DirectRefService implements RefService {

    private Class<?> serviceInterface;

    private String protocol;

    private InetSocketAddress inetSocketAddress;

    public DirectRefService(Class<?> serviceInterface, String protocol, String address) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        String[] hostAndPort = address.split(":");
        this.inetSocketAddress = new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
    }

    @Override
    public String getHost() {
        return inetSocketAddress.getHostName();
    }

    @Override
    public int getPort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public Class<?> getInterface() {
        return serviceInterface;
    }
}
