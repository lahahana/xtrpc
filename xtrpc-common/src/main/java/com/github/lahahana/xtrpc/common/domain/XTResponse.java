package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class XTResponse implements Serializable {

    private long requestId;

    private int statusCode;

    private Object result;

    private Throwable throwable;

}
