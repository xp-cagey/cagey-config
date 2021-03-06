package com.xpcagey.config.api;

import org.junit.Test;

import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;

public class ServiceLoaderTest {
    final Executor exec = Runnable::run; // inline execution
    final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void shouldFindConfigEngine() throws ConfigLoadException {
        ConfigSystem.reset(null);
        Config config = ConfigSystem.load("name", classLoader, exec); // should find TestConfigEngine which returns a mock(Config)
        assertEquals("From TestConfigEngine", config.getName());
    }
}
