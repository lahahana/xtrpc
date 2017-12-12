package com.github.lahahana.xtrpc.common.base;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public interface Holder<T> extends Destroyable {


    /**
     * @return unique-key equally one in the holder regardless is itself or not
     * */
    public T hold(T t);

    public void unhold(T t);

    public void holdAll(List<T> l);

    public List<T> listAll();

}
