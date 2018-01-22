package com.github.lahahana.xtrpc.test.api;


import com.github.lahahana.xtrpc.client.importer.DirectRefService;
import com.github.lahahana.xtrpc.client.importer.XTServiceImporter;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.util.NetworkUtil;
import com.github.lahahana.xtrpc.test.mock.service.ExceptionService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(JUnit4.class)
public class ExceptionalFunctionTest {

    private static Logger logger = LoggerFactory.getLogger(ExceptionalFunctionTest.class);
    private static String address = NetworkUtil.getLocalHostInetAddress().getHostAddress() + ":" + 8090;
    private static ExceptionService exceptionService;
    private static ExecutorService testExecutors = Executors.newFixedThreadPool(5);

    @BeforeClass
    public static void beforeSetup() {
        XTServiceImporter xtServiceImporter = new XTServiceImporter.Builder()
                .setApplication(new Application("DirectRefStyleClient"))
                .addDirectRefService(new DirectRefService(ExceptionService.class, address))
                .build();
        xtServiceImporter.doImport();
        exceptionService = xtServiceImporter.getRefService(ExceptionService.class);
    }

    @Before
    public void before() {

    }

    @AfterClass
    public static void afterClass() {
        testExecutors.shutdown();
    }

    @Test(expected = RuntimeException.class)
    public void testInvokeWithUnknownExceptionThrown() throws Exception {
        exceptionService.throwUnknownException();
    }

    @Test(expected = IOException.class)
    public void testInvokeWithKnownExceptionThrown() throws Exception {
        exceptionService.throwKnownException();
    }


}
