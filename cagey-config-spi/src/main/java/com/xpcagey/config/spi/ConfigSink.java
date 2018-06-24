package com.xpcagey.config.spi;

import java.time.Duration;
import java.time.Instant;

/**
 * ConfigSink represents an arbitrary command handler; it may be used to initialize the core package, take snapshots,
 * serialize a configuration update stream, or provide a diffing engine. The isSensitive flag will prevent the value
 * from being persisted if it is true.
 */
public interface ConfigSink {
    void set(String key, boolean isSensitive, boolean value);
    void set(String key, boolean isSensitive, Duration value);
    void set(String key, boolean isSensitive, double value);
    void set(String key, boolean isSensitive, Instant value);
    void set(String key, boolean isSensitive, long value);
    void set(String key, boolean isSensitive, String value);
    void forceDefault(String key); // overwrite explicit values with default
    void clear(String key);
}
