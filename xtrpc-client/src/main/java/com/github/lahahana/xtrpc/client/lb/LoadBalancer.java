package com.github.lahahana.xtrpc.client.lb;

import com.github.lahahana.xtrpc.client.skeleton.Invoker;
import com.github.lahahana.xtrpc.common.domain.Service;

import java.util.List;

public interface LoadBalancer {

    public Service selectService(List<Service> services);

    public Invoker selectInvoker(List<Invoker> invokers);

}
