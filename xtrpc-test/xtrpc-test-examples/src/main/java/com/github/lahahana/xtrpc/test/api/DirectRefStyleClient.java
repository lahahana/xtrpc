package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.client.importer.DirectRefService;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.domain.Address;
import com.github.lahahana.xtrpc.test.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DirectRefStyleClient {
    static Logger logger = LoggerFactory.getLogger(DirectRefStyleClient.class);
    static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() +":" + 8088;
    static String address2 = NetworkUtil.getLocalHostInetAddress().getHostAddress() +":" + 8089;
    static AddressService addressService;
    static ExecutorService testExecutors = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws Exception {
        XTServiceImporter context = new  XTServiceImporter.Builder()
                                    .addDirectRefService(new DirectRefService(AddressService.class, "xt", address))
                                    .addDirectRefService(new DirectRefService(AddressService.class, "xt", address2))
                                    .build();
        context.doImport();
        addressService = context.getRefService(AddressService.class);
        ArrayList<Long> costTimeList = new ArrayList();
        for (int i = 0; i < 100000; i++) {
            Future<Long> f = testExecutors.submit(new AddressTask());
            costTimeList.add(f.get());
        }

        long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
        logger.info("Average cost time:{}", averageCostTime);
//        testExecutors.shutdown();
    }

    static class AddressTask implements Callable<Long> {
        static Logger logger = LoggerFactory.getLogger(AddressTask.class);
        @Override
        public Long call() {
            long threadId = Thread.currentThread().getId();
            long startTime = System.currentTimeMillis();
            Address address = addressService.getAddressByUserId(threadId);
            long costTime = System.currentTimeMillis() - startTime;
            logger.info("result: {}, cost:{}", address, costTime);
            assert threadId == address.getRandomCode();
            return costTime;
        }
    }
}
