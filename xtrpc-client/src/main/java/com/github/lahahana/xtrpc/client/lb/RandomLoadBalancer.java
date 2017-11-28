package com.github.lahahana.xtrpc.client.lb;

import com.github.lahahana.xtrpc.common.domain.Service;

import java.util.List;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Service selectService(List<Service> services) {
        return services.get(0);
    }
}
