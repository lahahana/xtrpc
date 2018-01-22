package com.github.lahahana.xtrpc.common.stub;

import com.github.lahahana.xtrpc.common.base.Destroyable;

public interface XTStub extends Destroyable {

    public void bootstrap();

    public void shutdown();
}
