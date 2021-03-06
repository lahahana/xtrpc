package com.github.lahahana.xtrpc.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Service implements Serializable {

    @Getter
    private String serviceInterface;

    @Getter
    private String protocol;

    @Getter
    private String host;

    @Getter
    private int port;

    @Getter
    @Setter
    private boolean available;

    public Service(String serviceInterface, String protocol, String host, int port) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.available = true;
    }

    public Service(String serviceInterface, String protocol, String host, int port, boolean available) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.available = available;
    }

    public String getUniqueKey() {
        return serviceInterface + "-" + host + ":" + port;
    }

    @Override
    public String toString() {
        return getUniqueKey();
    }
}
