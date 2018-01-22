package com.github.lahahana.xtrpc.common.domain;

public interface Notifier<T> {

    public void notify(T msg);

}
