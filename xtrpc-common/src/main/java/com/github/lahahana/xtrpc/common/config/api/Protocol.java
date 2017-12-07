package com.github.lahahana.xtrpc.common.config.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Protocol {

    protected String name;

    protected String transporter;

    protected String serialization;

    protected int port;

    protected Protocol(){}
}
