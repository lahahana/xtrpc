package com.github.lahahana.xtrpc.client.importer;

import com.github.lahahana.xtrpc.common.config.api.Protocol;

public interface RefService {

    public String getHost();

    public int getPort();

    public Class<?> getInterface();

}
