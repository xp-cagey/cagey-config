package com.xpcagey.config.core;

import com.xpcagey.config.spi.ConfigServiceProvider;
import com.xpcagey.config.spi.ConfigSource;
import com.xpcagey.config.spi.IllegalPathException;

public class IllegalConfigServiceProvider implements ConfigServiceProvider {
    @Override public String getName() { return "illegal"; }
    @Override public ConfigSource load(ClassLoader loader, String path) throws IllegalPathException {
        throw new IllegalPathException(getName(), path, null);
    }
}
