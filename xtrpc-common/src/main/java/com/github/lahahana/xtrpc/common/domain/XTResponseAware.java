package com.github.lahahana.xtrpc.common.domain;

import lombok.Getter;

public class XTResponseAware extends AbstractAware<XTResponse> {

    @Getter
    private long requestId;

    private XTResponse response;

    public XTResponseAware(long requestId) {
        this.requestId = requestId;
    }

}
