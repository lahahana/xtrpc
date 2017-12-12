package com.github.lahahana.xtrpc.client.skeleton;

import com.github.lahahana.xtrpc.common.base.Holder;

import java.util.List;

public interface InvokerHolder extends Holder<Invoker> {

    /**
     * hold {@linkplain Invoker}, and mark {@linkplain com.github.lahahana.xtrpc.common.domain.Service} as available
     */
    public Invoker hold(Invoker invoker);

    /**
     * unhold {@linkplain Invoker}, and mark {@linkplain com.github.lahahana.xtrpc.common.domain.Service} as unavailable
     */
    public void unhold(Invoker invoker);

    public List<Invoker> listInvokersOfInterface(String interfaceName);

}
