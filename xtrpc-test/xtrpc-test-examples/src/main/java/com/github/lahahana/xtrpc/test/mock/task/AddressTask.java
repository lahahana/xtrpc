package com.github.lahahana.xtrpc.test.mock.task;

import com.github.lahahana.xtrpc.test.mock.domain.Address;
import com.github.lahahana.xtrpc.test.mock.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class AddressTask implements Callable<Long> {

    private static Logger logger = LoggerFactory.getLogger(AddressTask.class);

    private AddressService addressService;

    public AddressTask(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public Long call() {
        long threadId = Thread.currentThread().getId();
        long startTime = System.nanoTime();
        Address address = addressService.getAddressByUserId(threadId);
        long costTime = System.nanoTime() - startTime;
        logger.info("result: {}, cost:{}", address, costTime);
        assert threadId == address.getRandomCode();
        return costTime;
    }
}