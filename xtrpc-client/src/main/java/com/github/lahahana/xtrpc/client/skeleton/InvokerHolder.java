package com.github.lahahana.xtrpc.client.skeleton;

import java.util.List;

public interface InvokerHolder {

    public void holdInvoker(Invoker invoker);

    public void unholdInvoker(Invoker invoker);

    public List<Invoker> listInvokers();

    public List<Invoker> listInvokersOfInterface(String interfaceName);
}
