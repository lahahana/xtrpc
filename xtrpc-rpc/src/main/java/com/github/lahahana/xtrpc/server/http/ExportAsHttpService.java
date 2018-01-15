package com.github.lahahana.xtrpc.server.http;

public @interface ExportAsHttpService {

    /**
     * Service will only be exported as HTTP
     */
    boolean exportAsHttpServiceOnly() default false;

    int port() default 8081;
}
