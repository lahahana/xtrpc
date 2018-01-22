package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class XTResponse implements Serializable {

    private long requestId;

    private int statusCode;

    private Object result;

    private String throwableClass;

    private String throwable;

    public XTResponse(long requestId) {
        this.requestId = requestId;
    }
}
