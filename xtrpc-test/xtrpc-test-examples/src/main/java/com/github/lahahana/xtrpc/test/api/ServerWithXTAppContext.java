package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.server.export.XTApplicationContext;
import com.github.lahahana.xtrpc.test.service.impl.AddressServiceImpl;

public class ServerWithXTAppContext {

    public static void main(String[] args) throws Exception {
        new XTApplicationContext.Builder()
                                .setApplication(new Application("ServerWithXTAppContext"))
                                .setProtocol(new XTProtocol())
                                .addService(new AddressServiceImpl())
                                .build()
                                .start();
    }
}
