package com.github.lahahana.xtrpc.client.discovery;

import com.github.lahahana.xtrpc.common.base.Destroyable;
import com.github.lahahana.xtrpc.common.config.api.Reference;
import com.github.lahahana.xtrpc.common.config.api.Registry;
import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceNotFoundException;

import java.util.List;

public interface ServiceDiscoverer extends Destroyable {

    public List<Service> discoverService(Reference reference) throws ServiceNotFoundException;

    public Registry getRegistry();

}
