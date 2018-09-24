package com.xpcagey.config.api;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class TestConfigEngine implements ConfigEngine {
    Map<String, Object> defaults = new HashMap<>();

    @Override public void setDefault(String key, boolean value) { defaults.put(key, value); }
    @Override public void setDefault(String key, double value) { defaults.put(key, value); }
    @Override public void setDefault(String key, Duration value) { defaults.put(key, value); }
    @Override public void setDefault(String key, Instant value) { defaults.put(key, value); }
    @Override public void setDefault(String key, long value) { defaults.put(key, value); }
    @Override public void setDefault(String key, String value) { defaults.put(key, value); }
    @Override public Config load(String name, ClassLoader loader, Executor exec, Descriptor... descriptors) {
        Config config = mock(Config.class);
        when(config.getName()).thenReturn("From TestConfigEngine");
        return config;
    }
}
