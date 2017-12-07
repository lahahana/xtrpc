package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;

@Data
public class ServiceConfig {

    private String id;

    private Object ref;

    private Application application;

    private Protocol protocol;

}
