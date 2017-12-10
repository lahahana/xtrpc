package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;

@Data
public class ServiceConfig {

    private Application application;

    private Protocol protocol;

    private Registry registry;

    private Class<?> interfaceClass;

    private Object ref;



}
