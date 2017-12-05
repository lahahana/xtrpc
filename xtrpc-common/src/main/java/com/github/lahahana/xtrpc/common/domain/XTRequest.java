package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class XTRequest implements Serializable {

    @Getter private long requestId;

    private long timeout;

    private String interfaceClazz;

    private String method;

    private Class<?>[] argsType;

    private Object[] args;

    public XTRequest(long requestId) {
        this.requestId = requestId;
        this.timeout = 2000L;
    }

    public XTRequest(long requestId, String interfaceClazz, String method, Class<?>[] argsType, Object[] args) {
        this.requestId = requestId;
        this.interfaceClazz = interfaceClazz;
        this.method = method;
        this.argsType = argsType;
        this.args = args;
        this.timeout = 2000L;
    }
}
