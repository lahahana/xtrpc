package com.github.lahahana.xtrpc.test.service.impl;

import com.github.lahahana.xtrpc.test.domain.Address;
import com.github.lahahana.xtrpc.test.service.AddressService;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddressServiceImpl implements AddressService {
    @Override
    public Address getAddressByUserId(long id) {
        if(id == 1)
            throw new IllegalArgumentException();
        return new Address(571L, id,"浙江", "杭州", Stream.of("富阳区","西湖区").collect(Collectors.toList()));
    }

    @Override
    public Address getAddressByUserIdThrowException(long id) {
            throw new IllegalArgumentException("xxx");
    }
}
