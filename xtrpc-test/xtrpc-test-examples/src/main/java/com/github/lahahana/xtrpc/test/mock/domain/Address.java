package com.github.lahahana.xtrpc.test.mock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private long areaCode;

    private long randomCode;

    private String province;

    private String city;

    private List<String> subAreas;
}
