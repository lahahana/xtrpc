package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class Application {

    @Getter @Setter private String name;

    public Application(String name) {
        this.name = name;
    }
}
