package com.xpcagey.config.spi.helpers;

import com.xpcagey.config.spi.ConfigSink;
import com.xpcagey.config.spi.ValueCommand;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Simple sink consumes a ConfigSource and places the result into a HashMap; it is meant to be used for testing and
 * debugging implementations of ConfigSource inside their respective packages. A forced default is stored as a sentinel
 * value.  This implementation is not efficient under high loads and is not meant for production use.
 */
public class TestingSink implements ConfigSink {
    public static final Object USE_DEFAULT = new Object();
    private final HashMap<String, Object> values = new HashMap<>();
    private final HashMap<String, Object> sensitive = new HashMap<>();

    public Object get(String key, boolean isSensitive) {
        synchronized(values) {
            if (isSensitive)
                return sensitive.get(key);
            return values.get(key);
        }
    }

    public void addAll(Iterator<ValueCommand> it) {
        synchronized(values) {
            while(it.hasNext())
                it.next().apply(this);
        }
    }

    @Override public void set(String key, boolean isSensitive, boolean value) { put(key, isSensitive, value); }
    @Override public void set(String key, boolean isSensitive, Duration value) { put(key, isSensitive, value); }
    @Override public void set(String key, boolean isSensitive, double value) { put(key, isSensitive, value); }
    @Override public void set(String key, boolean isSensitive, Instant value) { put(key, isSensitive, value); }
    @Override public void set(String key, boolean isSensitive, long value) { put(key, isSensitive, value); }
    @Override public void set(String key, boolean isSensitive, String value) { put(key, isSensitive, value); }
    @Override public void forceDefault(String key) { put(key, false, USE_DEFAULT); }
    @Override public void clear(String key) {
        synchronized(values) {
            sensitive.remove(key);
            values.remove(key);
        }
    }

    private void put(String key, boolean isSensitive, Object value) {
        synchronized(values) {
            sensitive.remove(key);
            values.remove(key);
            if (isSensitive)
                sensitive.put(key, value);
            else
                values.put(key, value);
        }
    }
}
