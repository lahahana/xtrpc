package com.github.lahahana.xtrpc.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Service implements Serializable {

    @Getter
    private String serviceInterface;

    @Getter
    private String protocol;

    @Getter
    private String host;

    @Getter
    private int port;

    @Getter @Setter
    private boolean available;

    @Getter @Setter
    private boolean lazyInit;

    public Service(String serviceInterface, String protocol, String host, int port) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.available = true;
        this.lazyInit = false;
    }

    public String getUniqueKey() {
        return serviceInterface + "-" + host + ":" + port;
    }

    @Override
    public String toString() {
        return getUniqueKey();
    }
}
