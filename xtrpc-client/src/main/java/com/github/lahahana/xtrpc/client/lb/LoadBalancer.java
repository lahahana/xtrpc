package com.github.lahahana.xtrpc.client.lb;

import com.github.lahahana.xtrpc.common.domain.Service;

import java.util.List;

public interface LoadBalancer {

    public Service selectService(List<Service> services);

}
