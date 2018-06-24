package com.xpcagey.config.core;

import com.xpcagey.config.api.Descriptor;
import com.xpcagey.config.element.*;
import com.xpcagey.config.spi.ConfigSink;
import com.xpcagey.config.spi.ConfigSource;
import com.xpcagey.config.spi.ValueCommand;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

class ConfigModule implements Iterable<RawValueElement>, AutoCloseable {
    private final NavigableMap<String, RawValueElement> values = new ConcurrentSkipListMap<>();
    private final ConfigEventBindings<RawValueElement> bindings;
    private final Consumer<ValueCommand> sink;
    private final ConfigSource source;
    private final Descriptor desc;

    ConfigModule(Executor exec, Descriptor desc, ConfigSource source) {
        this.bindings = new ConfigEventBindings<>(exec);
        this.desc = desc;
        this.source = source;
        this.sink = new Sink();
        this.source.register(this.sink);
    }

    RawValueElement getOrNull(String key) { return values.get(key); }

    void addListener(Consumer<RawValueElement> listener) { bindings.addListener(listener); }
    void removeListener(Consumer<RawValueElement> listener) { bindings.removeListener(listener); }
    void addTrigger(String key, Consumer<RawValueElement> trigger) { bindings.addTrigger(key, trigger); }
    void removeTrigger(String key, Consumer<RawValueElement> trigger) { bindings.removeTrigger(key, trigger); }
    public Iterator<RawValueElement> iterator() { return values.values().iterator(); }

    public void close() { source.unregister(this.sink); }

    String getProvider() { return desc.getProvider(); }
    String getAlias() { return desc.getAlias(); }
    String getRawPath() { return desc.getRawPath(); }
    String getPath() { return source.getPath(); }

    void addReaper(Consumer<String> reaper) { bindings.addReaper(reaper); }
    void removeReaper(Consumer<String> reaper) { bindings.removeReaper(reaper); }

    private void update(RawValueElement e) {
        values.put(e.getKey(), e);
        bindings.notify(e);
    }

    class Sink implements ConfigSink, Consumer<ValueCommand> {
        @Override public void accept(ValueCommand command) { command.apply(this); }
        @Override public void set(String key, boolean isSensitive, boolean value) { update(new BooleanElement(key, isSensitive, value)); }
        @Override public void set(String key, boolean isSensitive, double value) { update(new DoubleElement(key, isSensitive, value)); }
        @Override public void set(String key, boolean isSensitive, Duration value) { update(new DurationElement(key, isSensitive, value)); }
        @Override public void set(String key, boolean isSensitive, Instant value) { update(new InstantElement(key, isSensitive, value)); }
        @Override public void set(String key, boolean isSensitive, long value) { update(new LongElement(key, isSensitive, value)); }
        @Override public void set(String key, boolean isSensitive, String value) { update(new StringElement(key, isSensitive, value)); }
        @Override public void forceDefault(String key) { update(new ResetToDefaultElement(key)); }
        @Override public void clear(String key) {
            values.remove(key);
            bindings.notifyRemoved(key);
        }
    }
}
