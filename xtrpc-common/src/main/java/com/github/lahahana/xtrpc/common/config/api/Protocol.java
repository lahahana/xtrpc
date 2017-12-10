package com.github.lahahana.xtrpc.common.config.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Protocol {

    private final String name;

    private final String transporter;

    private final String serialization;

    private final int port;

    public Protocol(){
        this("xt", "netty", "kryo", 8088);
    }

    public Protocol(String name, String transporter, String serialization, int port) {
        this.name = name;
        this.transporter = transporter;
        this.serialization = serialization;
        this.port = port;
    }
}
