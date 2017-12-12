package com.github.lahahana.xtrpc.test.task;

import com.github.lahahana.xtrpc.test.api.DirectRefStyleClient;
import com.github.lahahana.xtrpc.test.domain.Address;
import com.github.lahahana.xtrpc.test.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;


public class ExceptionAddressTask implements Callable<Long> {

    private static Logger logger = LoggerFactory.getLogger(DirectRefStyleClient.class);

    private AddressService addressService;

    public ExceptionAddressTask(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public Long call() {
        long threadId = Thread.currentThread().getId();
        long startTime = System.currentTimeMillis();
        Address address = addressService.getAddressByUserIdThrowException(threadId);
        long costTime = System.currentTimeMillis() - startTime;
        logger.info("result: {}, cost:{}", address, costTime);
        assert threadId == address.getRandomCode();
        return costTime;
    }
}