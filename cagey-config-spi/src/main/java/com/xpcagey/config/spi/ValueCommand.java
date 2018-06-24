package com.xpcagey.config.spi;

import java.time.Duration;
import java.time.Instant;

/**
 * ValueCommand provides a loose coupling between a ConfigSource and one or more ConfigSinks that allows updates after
 * initialization without a direct binding. Attempts to serialize a ValueCommand will fail to send the value; this has
 * been done on purpose to protect sensitive data. Any attempts to persist or transmit data must be done using an
 * implementation of ConfigSink instead of attempting to use ValueCommand directly.  Commands do not specify a source
 * explicitly and are assumed to be passed via a distinct stream for each source.
 */
public interface ValueCommand {
    void apply(ConfigSink sink);

    static ValueCommand clear(String key) { return new DoClear(key); }
    static ValueCommand forceDefault(String key) { return new DoForceDefault(key); }
    static ValueCommand set(String key, boolean sensitive, boolean value) { return new DoSetBoolean(key, sensitive, value); }
    static ValueCommand set(String key, boolean sensitive, Duration value) { return new DoSetDuration(key, sensitive, value); }
    static ValueCommand set(String key, boolean sensitive, double value) { return new DoSetDouble(key, sensitive, value); }
    static ValueCommand set(String key, boolean sensitive, Instant value) { return new DoSetInstant(key, sensitive, value); }
    static ValueCommand set(String key, boolean sensitive, long value) { return new DoSetLong(key, sensitive, value); }
    static ValueCommand set(String key, boolean sensitive, String value) { return new DoSetString(key, sensitive, value); }
}
