package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.Getter;

public class Service {

    @Getter private String interfaceClazz;

    @Getter private String host;

    @Getter private int port;

    @Getter private boolean available;

    public Service(String interfaceClazz, String host, int port) {
        this.interfaceClazz = interfaceClazz;
        this.host = host;
        this.port = port;
        this.available = true;
    }

    public String getUniqueKey() {
        return interfaceClazz+ "-" + host+ ":" + port;
    }

    @Override
    public String toString() {
        return interfaceClazz+ "-" + host+ ":" + port;
    }
}
