package com.github.lahahana.xtrpc.common.registry;

import com.github.lahahana.xtrpc.common.domain.Service;

import java.io.Closeable;

public interface ServiceRegistry extends Closeable {

    public void register(Service service);

    public void unregister(Service service);

}
