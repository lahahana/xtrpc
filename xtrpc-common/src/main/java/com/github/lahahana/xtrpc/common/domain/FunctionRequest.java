package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class FunctionRequest implements Serializable {

    private byte type;

    private long timeout = 2000L;

    public FunctionRequest(byte type) {
        this.type = type;
    }
}
