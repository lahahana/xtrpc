package com.github.lahahana.xtrpc.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.github.lahahana.xtrpc.common.domain.FunctionRequest;
import com.github.lahahana.xtrpc.common.domain.FunctionResponse;
import com.github.lahahana.xtrpc.common.domain.XTRequest;
import com.github.lahahana.xtrpc.common.domain.XTResponse;

public class KryoPoolFactory {

    private static volatile KryoPoolFactory instance;

    private KryoFactory kryoFactory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(XTRequest.class);
            kryo.register(XTResponse.class);
            kryo.register(FunctionRequest.class);
            kryo.register(FunctionResponse.class);
            kryo.register(Throwable.class);
            return kryo;
        }
    };

    private KryoPool kryoPool = new KryoPool.Builder(kryoFactory).build();

    private KryoPoolFactory() {
    }

    public static KryoPoolFactory getInstance() {
        if(instance == null) {
            synchronized (KryoPoolFactory.class) {
                if(instance == null) {
                    instance = new KryoPoolFactory();
                }
            }
        }
        return instance;
    }

    public KryoPool getKryoPool() {
        return kryoPool;
    }
}
