package com.github.lahahana.xtrpc.common.domain;

import com.github.lahahana.xtrpc.common.exception.TimeoutException;
import lombok.Getter;
import lombok.Setter;

public class XTResponseAware extends AbstractAware<XTResponse> {

    @Getter private long requestId;

    private XTResponse response;

    public XTResponseAware(long requestId) {
        this.requestId = requestId;
    }

}
