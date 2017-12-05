package com.github.lahahana.xtrpc.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FunctionResponse {

    private byte type;

    private Object result;

    public FunctionResponse(byte type) {
        this.type = type;
    }
}
