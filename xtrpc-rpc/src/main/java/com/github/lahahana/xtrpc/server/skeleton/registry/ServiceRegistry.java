package com.github.lahahana.xtrpc.server.skeleton.registry;

import com.github.lahahana.xtrpc.common.domain.Service;
import com.github.lahahana.xtrpc.common.exception.ServiceRegisterException;

import java.io.Closeable;

public interface ServiceRegistry extends Closeable {

    /**
     *@@throws {@link ServiceRegisterException} when service register fail
     * */
    public void register(Service service)  throws ServiceRegisterException;

    /**
     * subclass must not throw any exception even service unregister fail
     * */
    public boolean unregister(Service service) ;

}
