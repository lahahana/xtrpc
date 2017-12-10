package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.config.api.Reference;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;

import java.io.Closeable;
import java.util.List;

public interface ServiceDiscoverer extends Closeable {

    public List<Service> discoverService(Reference reference) throws ServiceNotFoundException;

}
