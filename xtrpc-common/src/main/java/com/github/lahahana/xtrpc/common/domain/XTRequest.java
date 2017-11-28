package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class XTRequest implements Serializable {

    @Getter private long requestId;

    private long timeout;

    private String interfaceClazz;

    private String method;

    private Class<?>[] argsType;

    private Object[] args;

}
