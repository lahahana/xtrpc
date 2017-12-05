package com.github.lahahana.xtrpc.test.client;

import com.github.lahahana.xtrpc.client.proxy.RPCClientProxy;
import com.github.lahahana.xtrpc.test.domain.Address;
import com.github.lahahana.xtrpc.test.domain.User;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ClientMock {

    static Logger logger = LoggerFactory.getLogger(ClientMock.class);

    static UserService userService = getUserServiceInMockSpringContext();

    static AddressService addressService = getAddessServiceInMockSpringContext();

    static ExecutorService testExecutors = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
//        User user = new User(0, "Leon", 20);
//        testExecutors.execute(new UserTask());
        ArrayList<Long> costTimeList = new ArrayList();
        for (int i = 0; i < 10000; i++) {
            Future<Long> f = testExecutors.submit(new AddressTask());
            costTimeList.add(f.get());
        }

        long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
        logger.info("Average cost time:{}", averageCostTime);

    }

    static UserService getUserServiceInMockSpringContext() {
        return (UserService) RPCClientProxy.getProxy(UserService.class);
    }

    static AddressService getAddessServiceInMockSpringContext() {
        return (AddressService) RPCClientProxy.getProxy(AddressService.class);
    }

    static class AddressTask implements Callable<Long> {
        @Override
        public Long call() {
            logger.info("print result----AddressService-------:");
            long threadId = Thread.currentThread().getId();
            long startTime = System.currentTimeMillis();
            Address address = addressService.getAddressByUserId(threadId);
            long costTime = System.currentTimeMillis() - startTime;
            logger.info("result: {}, cost:{}", address, costTime);
            assert threadId == address.getRandomCode();
            return costTime;
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
