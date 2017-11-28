package com.github.lahahana.xtrpc.common.domain;

import lombok.Getter;
import lombok.Setter;

public class XTResponseAware implements Aware {

    @Getter private long requestId;

    @Getter @Setter private XTResponse response;

    public XTResponseAware(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public void aware() {

    }
}
