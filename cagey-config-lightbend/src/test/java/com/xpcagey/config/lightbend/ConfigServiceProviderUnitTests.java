package com.xpcagey.config.lightbend;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigServiceProviderUnitTests {
    @Test
    public void checkName() {
        assertEquals("lightbend", new ConfigServiceProvider().getName());
    }

    @Test
    public void checkOutput() {
        assertNotNull(new ConfigServiceProvider().load(getClass().getClassLoader(), ""));
    }
}
