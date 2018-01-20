package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class XTRequest implements Serializable {

    @Getter
    private long requestId;

    private long timeout;

    private String interfaceName;

    private String method;

    private Class<?>[] argsType;

    private Object[] args;

    public XTRequest(long requestId) {
        this.requestId = requestId;
        this.timeout = 2000L;
    }

    public XTRequest(long requestId, String interfaceName, String method, Class<?>[] argsType, Object[] args) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.method = method;
        this.argsType = argsType;
        this.args = args;
        this.timeout = 2000L;
    }
}
