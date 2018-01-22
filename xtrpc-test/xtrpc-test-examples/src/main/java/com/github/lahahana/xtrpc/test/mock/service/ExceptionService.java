package com.github.lahahana.xtrpc.test.mock.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ExceptionService {

    void throwUnknownException();

    void throwKnownException() throws FileNotFoundException, IOException;

    void throwClientSideNotExistsException();

}
