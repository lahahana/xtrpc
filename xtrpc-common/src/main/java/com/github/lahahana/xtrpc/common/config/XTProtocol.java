package com.github.lahahana.xtrpc.common.config;

import com.github.lahahana.xtrpc.common.config.api.Protocol;

public class XTProtocol extends Protocol {

    public XTProtocol() {
        this(8088);
    }

    public XTProtocol(int port) {
        super("xt", "netty", "kryo", port);
    }

}
