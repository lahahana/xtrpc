package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.server.exporter.XTServiceExporter;
import com.github.lahahana.xtrpc.test.service.impl.AddressServiceImpl;
import com.github.lahahana.xtrpc.test.service.impl.UserServiceImpl;

public class XTServer {

    public static void main(String[] args) throws Exception {
        XTProtocol protocol = new XTProtocol(8088);
        new XTServiceExporter.Builder()
                                .setApplication(new Application("ServerWithXTAppContext"))
                                .setProtocol(protocol)
                                .addServiceConfig(new AddressServiceImpl())
                                .build()
                                .doExport();

        XTProtocol protocol2 = new XTProtocol(8089);
        new XTServiceExporter.Builder()
                .setApplication(new Application("ServerWithXTAppContext2"))
                .setProtocol(protocol2)
                .addServiceConfig(new UserServiceImpl())
                .build()
                .doExport();
    }
}
