package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.common.util.Tuple;

import java.net.InetSocketAddress;

public class DirectRefService implements RefService {

    private Class<?> serviceInterface;

    private InetSocketAddress inetSocketAddress;

    public DirectRefService(Class<?> serviceInterface, String address) {
        this.serviceInterface = serviceInterface;
        Tuple<String, Integer> hostPortPair = NetworkUtil.assembleHostPortPair(address);
        this.inetSocketAddress = new InetSocketAddress(hostPortPair.getK(), hostPortPair.getV());
    }

    /**
     * @return host of ref service
     */
    public String getHost() {
        return inetSocketAddress.getHostName();
    }

    /**
     * @return port of ref service
     */
    public int getPort() {
        return inetSocketAddress.getPort();
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    @Override
    public Class<?> getInterface() {
        return serviceInterface;
    }
}
