package com.github.lahahana.xtrpc.client.lb;

import com.github.lahahana.xtrpc.client.skeleton.Invoker;
import com.github.lahahana.xtrpc.common.domain.Service;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    Random random = new Random();

    @Override
    public Service selectService(List<Service> services) {
        return services.get(random.nextInt(services.size()));
    }

    @Override
    public Invoker selectInvoker(List<Invoker> invokers) {
        return invokers.get(random.nextInt(invokers.size()));
    }
}
