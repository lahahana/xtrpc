package com.github.lahahana.xtrpc.test.server;

import com.github.lahahana.xtrpc.server.ServerStub;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.service.UserService;
import com.github.lahahana.xtrpc.test.service.impl.AddressServiceImpl;
import com.github.lahahana.xtrpc.test.service.impl.UserServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMockSpr {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("service.xml");
        context.start();
        System.out.println("server mock started");
    }
}
