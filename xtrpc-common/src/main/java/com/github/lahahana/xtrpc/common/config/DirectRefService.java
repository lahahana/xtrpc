package com.github.lahahana.xtrpc.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DirectRefService {

    private Class<?> serviceInterface;

    private String protocol;

    private String address;

}
