package com.github.lahahana.xtrpc.test.api;

import com.github.lahahana.xtrpc.client.importer.DirectRefService;
import com.github.lahahana.xtrpc.client.importer.RegistryRefService;
import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.common.config.XTProtocol;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.service.AddressService;
import com.github.lahahana.xtrpc.test.task.AddressTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegistryRefServiceClient {

    static Logger logger = LoggerFactory.getLogger(DirectRefStyleClient.class);
    static String redisRegistryAddress = "127.0.0.1";
    static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8088;
    static String address2 = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8089;
    static AddressService addressService;
    static ExecutorService testExecutors = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        Application application = new Application("DirectRefStyleClient");
        Registry registry = new Registry("redis", redisRegistryAddress, 6379);
        RegistryRefService registryRefService = new RegistryRefService(AddressService.class, registry);
        XTServiceImporter xtServiceImporter = new XTServiceImporter.Builder()
                .setApplication(application)
                .addRegistryRefService(registryRefService)
                .build();
        xtServiceImporter.doImport();
        addressService = xtServiceImporter.getRefService(AddressService.class);
        ArrayList<Long> costTimeList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            Future<Long> f = testExecutors.submit(new AddressTask(addressService));
            costTimeList.add(f.get());
        }

        long averageCostTime = costTimeList.stream().reduce((x, y) -> x + y).get() / costTimeList.size();
        logger.info("Average cost time:{}", averageCostTime);
//        testExecutors.shutdown();
    }


}
