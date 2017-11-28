package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;

@Data
public class Protocol {

    private String name;

    private String transporter;

    private String serialization;

}
