package com.github.lahahana.xtrpc.common.domain;

import lombok.Getter;

public class Service {

    @Getter private String serviceInterface;

    @Getter private String protocol;

    @Getter private String address;

    @Getter private boolean available;

    @Getter private boolean lazyInit;

    public Service(String serviceInterface, String protocol, String address) {
        this.serviceInterface = serviceInterface;
        this.protocol = protocol;
        this.address = address;
        this.available = true;
        this.lazyInit = false;
    }

    public String getUniqueKey() {
        return serviceInterface + "-" + address;
    }

    @Override
    public String toString() {
        return serviceInterface + "-" + address;
    }
}
