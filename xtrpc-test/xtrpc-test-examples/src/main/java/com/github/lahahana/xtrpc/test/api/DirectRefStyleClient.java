package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.importer.DirectRefService;
import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.task.AddressTask;
import com.github.lahahana.xtrpc.test.task.ExceptionAddressTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DirectRefStyleClient {
    static Logger logger = LoggerFactory.getLogger(DirectRefStyleClient.class);
    static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8088;
    static String address2 = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8089;
    static AddressService addressService;
    static ExecutorService testExecutors = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws Exception {
        XTServiceImporter xtServiceImporter = new XTServiceImporter.Builder()
                .setApplication(new Application("DirectRefStyleClient"))
                .addDirectRefService(new DirectRefService(AddressService.class, address))
//                .addDirectRefService(new DirectRefService(AddressService.class, address2))
                .build();
        xtServiceImporter.doImport();
        addressService = xtServiceImporter.getRefService(AddressService.class);

        List<Long> costTimeList = Collections.synchronizedList(new ArrayList<Long>());
        for (int i = 0; i < 1; i++) {
            Future<Long> f = testExecutors.submit(new ExceptionAddressTask(addressService));
            costTimeList.add(f.get());
        }

        long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
        costTimeList.stream().forEach((time) -> {
            logger.info("{}", time);
        });
        logger.info("Total task:{}, Average cost time:{}", costTimeList.size(), averageCostTime);
        testExecutors.shutdown();
    }
}
