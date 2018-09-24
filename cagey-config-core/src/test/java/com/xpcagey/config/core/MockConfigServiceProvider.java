package com.xpcagey.config.core;

import com.xpcagey.config.spi.ConfigServiceProvider;
import com.xpcagey.config.spi.ConfigSink;
import com.xpcagey.config.spi.ConfigSource;
import com.xpcagey.config.spi.ValueCommand;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

public class MockConfigServiceProvider implements ConfigServiceProvider {
    @Override public String getName() { return "mock"; }
    @Override public ConfigSource load(ClassLoader loader, String path) {
        return new ConfigSource(){
            @Override public Iterator<ValueCommand> iterator() { return Collections.emptyIterator(); }
            @Override public void close() {}
            @Override public String getPath() { return path; }
            @Override public void initialize(ConfigSink sink) {}
            @Override public void register(Consumer<ValueCommand> event) {}
            @Override public void unregister(Consumer<ValueCommand> event) {}
        };
    }
}
