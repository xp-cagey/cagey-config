package com.xpcagey.config.api;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;

public interface ConfigEngine {
    void setDefault(String key, boolean value);
    void setDefault(String key, double value);
    void setDefault(String key, Duration value);
    void setDefault(String key, Instant value);
    void setDefault(String key, long value);
    void setDefault(String key, String value);

    Config load(String name, Executor exec, Descriptor... descriptors) throws ConfigLoadException;
}
