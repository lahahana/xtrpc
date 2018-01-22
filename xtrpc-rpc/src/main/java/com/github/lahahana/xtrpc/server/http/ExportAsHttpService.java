package com.github.lahahana.xtrpc.server.http;

public @interface ExportAsHttpService {

    /**
     * Service will only be exported as HTTP
     */
    boolean exportAsHttpServiceOnly() default false;

    /**
     * the http port of http service
     */
    int port() default 8081;

    /**
     * Default value will be the path of annotated interface's package name plus method name,
     * "com.github.lahahana.demo.service.UserService.getUserById()" -> /com/github/lahahana/demo/service/UserService/getUserById
     */
    String path() default "";

}
