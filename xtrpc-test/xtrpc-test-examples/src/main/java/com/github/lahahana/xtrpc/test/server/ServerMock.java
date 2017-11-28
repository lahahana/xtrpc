package com.github.lahahana.xtrpc.test.server;

import com.github.lahahana.xtrpc.common.exception.StubInitializeException;
import com.github.lahahana.xtrpc.server.ServerStub;
import com.github.lahahana.xtrpc.test.client.ClientMock;
import com.github.lahahana.xtrpc.test.domain.User;
import com.github.lahahana.xtrpc.test.domain.UserOuterClass;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.service.UserService;
import com.github.lahahana.xtrpc.test.service.impl.AddressServiceImpl;
import com.github.lahahana.xtrpc.test.service.impl.UserServiceImpl;

import java.util.List;

public class ServerMock {

    public static void main(String[] args) throws StubInitializeException {
        UserService userService = new UserServiceImpl();
        AddressService addressService = new AddressServiceImpl();
        ServerStub serverStub = new ServerStub();
        serverStub.addMockService(userService.getClass().getInterfaces()[0].getName(), userService);
        serverStub.addMockService(addressService.getClass().getInterfaces()[0].getName(), addressService);
        serverStub.start();
    }
}
