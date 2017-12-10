package com.github.lahahana.xtrpc.common.config.api;

import lombok.Data;

@Data
public class Reference {

    private String interfaceName;

    private Protocol protocol;

    private Registry registry;


    private int maxRefServiceNum = 10;

    private int minRefServiceNum = 1;

    private int initRefServiceNum = 3;

}
