package com.github.lahahana.xtrpc.test.mock.service;

import com.github.lahahana.xtrpc.test.mock.domain.Address;

public interface AddressService {

    Address getAddressByUserId(long id);

    Address getAddressByUserIdThrowException(long id);
}
