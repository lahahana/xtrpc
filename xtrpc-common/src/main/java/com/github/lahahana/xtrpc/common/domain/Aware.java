package com.github.lahahana.xtrpc.common.domain;

import com.github.lahahana.xtrpc.common.exception.TimeoutException;

public interface Aware<R> extends Notifier<R> {

    public R aware();

    public R aware(long timeout) throws TimeoutException;

}
