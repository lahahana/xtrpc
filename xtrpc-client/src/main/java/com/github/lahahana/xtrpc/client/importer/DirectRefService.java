package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.common.util.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.INTERNAL;

import java.net.InetSocketAddress;

@Getter
@Setter
public class DirectRefService implements RefService {

    private Class<?> serviceInterface;

    private Protocol protocol;

    private InetSocketAddress inetSocketAddress;

    public DirectRefService(Class<?> serviceInterface, Protocol protocol, String address) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        Tuple<String, Integer> hostPortPair = NetworkUtil.assembleHostPortPair(address);
        this.inetSocketAddress = new InetSocketAddress(hostPortPair.getK(), hostPortPair.getV());
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
