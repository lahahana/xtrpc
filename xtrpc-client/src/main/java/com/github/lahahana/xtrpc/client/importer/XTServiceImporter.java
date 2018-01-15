package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.client.skeleton.proxy.XTRpcClientProxy;
import com.github.lahahana.xtrpc.client.skeleton.stub.ClientStub;
import com.github.lahahana.xtrpc.client.skeleton.stub.ClientStubFactory;
import com.github.lahahana.xtrpc.common.config.api.Application;
import com.github.lahahana.xtrpc.common.config.api.Protocol;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.NoAvailableServicesException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotAvailableException;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;
import com.github.lahahana.xtrpc.common.util.AssertsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for import remote ref service in direct ref style or registry ref style
 */
public final class XTServiceImporter {

    private Application application;
    private List<DirectRefService> directRefServices;
    private List<RegistryRefService> registryRefServices;
    private boolean ignorePartialOffline;
    private volatile boolean purge;

    private XTServiceImporter(XTServiceImporter.Builder builder) {
        this.application = builder.application;
        this.directRefServices = builder.directRefServices;
        this.registryRefServices = builder.registryRefServices;
        this.ignorePartialOffline = builder.ignorePartialOffline;
    }

    public void doImport() {
        ClientStub stub = ClientStubFactory.getInstance().getClientStub(application.getProtocol());
        Map<String, Integer> sameRefServiceCountMap = new HashMap<>();

        //do statistics of direct ref service
        for (DirectRefService directRefService : directRefServices) {
            final String interfaceName = directRefService.getInterface().getName();
            if (sameRefServiceCountMap.containsKey(interfaceName)) {
                int count = sameRefServiceCountMap.get(interfaceName);
                sameRefServiceCountMap.put(interfaceName, count++);
            } else {
                sameRefServiceCountMap.put(interfaceName, 1);
            }
        }

        Map<String, Integer> unavailableRefServiceCountMap = new HashMap<>();
        directRefServices.parallelStream().forEach((directRefService) -> {
            final String interfaceName = directRefService.getInterface().getName();
            Service service = new Service(interfaceName, null, directRefService.getHost(), directRefService.getPort());
            try {
                stub.initRefService(service);
            } catch (ServiceNotAvailableException e) {
                //TO-DO performance enhance: partial direct ref service unavailable tolerance
                if (ignorePartialOffline) {
                    synchronized (unavailableRefServiceCountMap) {
                        Integer count = unavailableRefServiceCountMap.get(service.getServiceInterface());
                        if (count == null) {
                            unavailableRefServiceCountMap.put(interfaceName, 1);
                        } else {
                            unavailableRefServiceCountMap.put(interfaceName, ++count);
                            int sameRefServiceNumber = sameRefServiceCountMap.get(service.getServiceInterface());
                            boolean sameRefServiceExists = sameRefServiceNumber > 1 ? true : false;
                            if (sameRefServiceExists && count < sameRefServiceNumber) {
                                //do-nothing
                            } else {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });

        registryRefServices.parallelStream().forEach((s) -> {
            try {
                stub.initRegistryRefService(s);
            } catch (ServiceNotFoundException | NoAvailableServicesException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> T getRefService(Class<T> serviceInterface) {
        return getRefService0(serviceInterface, application.getProtocol());
    }

    private <T> T getRefService0(Class<T> serviceInterface, Protocol protocol) {
        return XTRpcClientProxy.getProxy(serviceInterface, protocol);
    }

    public static class Builder {

        private Application application;
        private List<DirectRefService> directRefServices = new ArrayList<>();
        private List<RegistryRefService> registryRefServices = new ArrayList<>();
        private boolean ignorePartialOffline = false;

        public Builder() {
        }

        public Builder setApplication(Application application) {
            this.application = application;
            return this;
        }

        public Builder addRegistryRefService(RegistryRefService service) {
            if (!service.getServiceInterface().isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            registryRefServices.add(service);
            return this;
        }

        /**
         * Used for direct ref service , skip service discovery through registry
         */
        public Builder addDirectRefService(DirectRefService service) {
            if (!service.getInterface().isInterface()) {
                throw new IllegalArgumentException("parameter must be interface");
            }
            directRefServices.add(service);
            return this;
        }

        /**
         * @param ignorePartialOffline will ignore partial direct ref service unavailable if set to true, default false
         */
        public Builder setIgnorePartialOffline(boolean ignorePartialOffline) {
            this.ignorePartialOffline = ignorePartialOffline;
            return this;
        }

        public XTServiceImporter build() {
            AssertsUtil.ensureNotNull("application", application);
            return new XTServiceImporter(this);
        }

    }
}
