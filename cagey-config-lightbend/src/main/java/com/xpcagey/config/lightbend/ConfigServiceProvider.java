package com.xpcagey.config.lightbend;

public class ConfigServiceProvider implements com.xpcagey.config.spi.ConfigServiceProvider {
    @Override public String getName() { return "lightbend"; }
    @Override public com.xpcagey.config.spi.ConfigSource load(String path) { return new ConfigSource(path); }
}
