package com.xpcagey.config.spi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExceptionUnitTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void illegalPathException() throws IllegalPathException {
        thrown.expect(IllegalPathException.class);
        thrown.expectMessage("Path [path-x] is not supported for loader [loader-x]");

        throw new IllegalPathException("loader-x", "path-x", new Exception());
    }
}
