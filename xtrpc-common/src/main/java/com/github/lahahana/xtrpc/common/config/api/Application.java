package com.github.lahahana.xtrpc.common.config.api;

import com.github.lahahana.xtrpc.common.config.XTProtocol;
import lombok.Getter;
import lombok.Setter;

public class Application {

    @Getter
    private final String name;

    @Getter
    @Setter
    private Protocol protocol;

    public Application(String name) {
        this.name = name;
        this.protocol = new XTProtocol();
    }

    public Application(String name, Protocol protocol) {
        this.name = name;
        this.protocol = protocol;
    }
}
