package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.server.exporter.XTServiceExporter;
import com.github.lahahana.xtrpc.test.mock.service.AddressService;
import com.github.lahahana.xtrpc.test.mock.service.ExceptionService;
import com.github.lahahana.xtrpc.test.mock.service.UserService;
import com.github.lahahana.xtrpc.test.mock.service.impl.AddressServiceImpl;
import com.github.lahahana.xtrpc.test.mock.service.impl.ExceptionServiceImpl;
import com.github.lahahana.xtrpc.test.mock.service.impl.UserServiceImpl;

public class XTServer {

    static String redisRegistryAddress = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        XTProtocol protocol = new XTProtocol(8088);
        Registry registry = new Registry("redis", redisRegistryAddress, 6379);
        new XTServiceExporter.Builder()
                .setApplication(new Application("RpcServiceEstablishedByExporter"))
                .setProtocol(protocol)
                .setService(AddressService.class, new AddressServiceImpl())
                .setRegistry(registry)
                .build()
                .doExport();

        XTProtocol protocol2 = new XTProtocol(8089);
        new XTServiceExporter.Builder()
                .setApplication(new Application("RpcServiceEstablishedByExporter-2"))
                .setProtocol(protocol2)
                .setRegistry(registry)
                .setService(UserService.class, new UserServiceImpl())
                .build()
                .doExport();

        XTProtocol protocol3 = new XTProtocol(8090);
        new XTServiceExporter.Builder()
                .setApplication(new Application("RpcServiceEstablishedByExporter-3"))
                .setProtocol(protocol3)
                .setRegistry(registry)
                .setService(ExceptionService.class, new ExceptionServiceImpl())
                .build()
                .doExport();
    }
}
