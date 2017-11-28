package com.github.lahahana.xtrpc.test.client;

import com.github.lahahana.xtrpc.client.proxy.RPCClientProxy;
import com.github.lahahana.xtrpc.test.domain.Address;
import com.github.lahahana.xtrpc.test.domain.User;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMockSpr {

    static Logger logger = LoggerFactory.getLogger(ClientMockSpr.class);

    static UserService userService = getUserServiceInMockSpringContext();

    static AddressService addressService = getAddessServiceInMockSpringContext();

    static ExecutorService testExecutors = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
//        User user = new User(0, "Leon", 20);
        testExecutors.execute(new ClientMock.UserTask());
        for (int i = 0; i < 10; i++) {
            testExecutors.execute(new ClientMock.AddressTask());
        }

    }

    static UserService getUserServiceInMockSpringContext() {
        return (UserService) RPCClientProxy.getProxy(UserService.class);
    }

    static AddressService getAddessServiceInMockSpringContext() {
        return (AddressService) RPCClientProxy.getProxy(AddressService.class);
    }

    static class AddressTask implements Runnable {
        @Override
        public void run() {
            logger.info("print result----AddressService-------:");
            long threadId = Thread.currentThread().getId();
            Address address = addressService.getAddressByUserId(threadId);
            assert threadId == address.getRandomCode();
        }
    }

    static class UserTask implements Runnable {
        @Override
        public void run() {
            List<User> relatedUsers = userService.getAllUsers();
            logger.info("print result----UserService-------:");
            relatedUsers.stream().forEach(System.out::println);
        }
    }
}
