package com.github.lahahana.xtrpc.test.service;

import com.github.lahahana.xtrpc.test.domain.Address;

public interface AddressService {

    public Address getAddressByUserId(long id);
    public Address getAddressByUserIdThrowException(long id);
}
