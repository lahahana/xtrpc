package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.importer.RegistryRefService;
import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.test.mock.service.AddressService;
import com.github.lahahana.xtrpc.test.mock.task.AddressTask;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class RegistryRefServiceFunctionTest {

    private static Logger logger = LoggerFactory.getLogger(RegistryRefServiceFunctionTest.class);
    private static String redisRegistryAddress = "127.0.0.1";
    private static AddressService addressService;
    private static ExecutorService testExecutors = Executors.newFixedThreadPool(1);

    @BeforeClass
    public static void beforeSetup() {
        Registry registry = new Registry("redis", redisRegistryAddress, 6379);
        RegistryRefService registryRefService = new RegistryRefService(AddressService.class, registry);
        XTServiceImporter xtServiceImporter = new XTServiceImporter.Builder()
                .setApplication(new Application("RegistryRefStyleClient-1"))
                .addRegistryRefService(registryRefService)
                .build();
        xtServiceImporter.doImport();
        addressService = xtServiceImporter.getRefService(AddressService.class);
    }

    @AfterClass
    public static void afterClass() {
        testExecutors.shutdown();
    }

    @Test
    public void testRegistryRefService() {
        for (int i = 0; i < 10; i++) {
            Future<Long> f = testExecutors.submit(new AddressTask(addressService));
        }
    }


}
