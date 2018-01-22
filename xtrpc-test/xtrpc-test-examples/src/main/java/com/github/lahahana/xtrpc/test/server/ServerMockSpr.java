package com.github.lahahana.xtrpc.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMockSpr {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("service.xml");
        context.start();
        System.out.println("server mock started");
    }
}
