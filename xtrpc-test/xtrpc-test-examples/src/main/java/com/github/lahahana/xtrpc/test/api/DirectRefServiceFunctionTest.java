package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.importer.DirectRefService;
import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.mock.service.AddressService;
import com.github.lahahana.xtrpc.test.mock.service.ExceptionService;
import com.github.lahahana.xtrpc.test.mock.task.AddressTask;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(JUnit4.class)
public class DirectRefServiceFunctionTest {
    private static Logger logger = LoggerFactory.getLogger(DirectRefServiceFunctionTest.class);
    private static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8088;
    private static AddressService addressService;
    private static ExecutorService testExecutors = Executors.newFixedThreadPool(1);

    @BeforeClass
    public static void beforeSetup() {
        XTServiceImporter xtServiceImporter = new XTServiceImporter.Builder()
                .setApplication(new Application("DirectRefStyleClient-1"))
                .addDirectRefService(new DirectRefService(ExceptionService.class, address))
                .build();
        xtServiceImporter.doImport();
        addressService = xtServiceImporter.getRefService(AddressService.class);
    }

    @AfterClass
    public static void afterClass() {
        testExecutors.shutdown();
    }

    @Test
    public void testDirectRefService() throws Exception {
        try {
            List<Long> costTimeList = Collections.synchronizedList(new ArrayList<Long>());
            for (int i = 0; i < 1; i++) {
                Future<Long> f = testExecutors.submit(new AddressTask(addressService));
                costTimeList.add(f.get());
            }

            long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
            costTimeList.stream().forEach((time) -> {
                logger.info("{}", time);
            });
            logger.info("Total task:{}, Average cost time:{}", costTimeList.size(), averageCostTime);
        } finally {
            while (testExecutors.isTerminated()) {
                testExecutors.shutdown();
            }
        }
    }
}
