package com.xpcagey.config.spi;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValueCommandIterable implements Iterable<ValueCommand> {
    private final List<ValueCommand> list = new ArrayList<>();

    @Override public Iterator<ValueCommand> iterator() { return list.iterator(); }

    public ValueCommandIterable(ConfigSource src) {
        src.initialize(new ConfigSink() {
            @Override public void set(String key, boolean sensitive, boolean value) { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void set(String key, boolean sensitive, Duration value)  { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void set(String key, boolean sensitive, double value)  { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void set(String key, boolean sensitive, Instant value)  { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void set(String key, boolean sensitive, long value)  { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void set(String key, boolean sensitive, String value) { list.add(ValueCommand.set(key, sensitive, value)); }
            @Override public void forceDefault(String key) { list.add(ValueCommand.forceDefault(key)); }
            @Override public void clear(String key) { list.add(ValueCommand.clear(key)); }
        });
    }
}
