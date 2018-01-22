package com.github.lahahana.xtrpc.test.mock.service.impl;

import com.github.lahahana.xtrpc.test.mock.service.ExceptionService;

import java.io.IOException;

public class ExceptionServiceImpl implements ExceptionService {

    @Override
    public void throwUnknownException() {
        throw new IllegalStateException();
    }

    @Override
    public void throwKnownException() throws IOException {
        throw new IOException();
    }

    @Override
    public void throwClientSideNotExistsException() {

    }
}
