package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;
import lombok.Getter;

@Getter
public class Registry {

    private String type;

    private String host;

    private int port;

    public Registry(String type, String host, int port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }
}
