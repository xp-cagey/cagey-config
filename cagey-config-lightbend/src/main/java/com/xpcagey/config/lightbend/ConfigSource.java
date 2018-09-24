package com.xpcagey.config.lightbend;

import com.typesafe.config.*;
import com.xpcagey.config.spi.ConfigSink;
import com.xpcagey.config.spi.ValueCommand;

import java.util.*;
import java.util.function.Consumer;

import com.xpcagey.config.spi.ValueCommandIterable;

class ConfigSource implements com.xpcagey.config.spi.ConfigSource {
    private final String path;
    private final Config config;

    ConfigSource(ClassLoader loader, String path) {
        this.path = path;
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.isEmpty())
            config = ConfigFactory.load(loader);
        else
            config = ConfigFactory.load(loader, path);
    }

    @Override public String getPath() { return path; }
    @Override public Iterator<ValueCommand> iterator() { return new ValueCommandIterable(this).iterator(); }
    @Override public void register(Consumer<ValueCommand> event) {}
    @Override public void unregister(Consumer<ValueCommand> event) {}
    @Override public void close() {}

    @Override public void initialize(ConfigSink sink) {
        Queue<Map.Entry<String, ConfigValue>> entries = new LinkedList<>();
        entries.addAll(config.root().entrySet());
        while(!entries.isEmpty()) {
            Map.Entry<String, ConfigValue> entry = entries.remove();
            String key = entry.getKey();
            ConfigValue value = entry.getValue();
            switch (value.valueType()) {
                case BOOLEAN:
                    sink.set(key, false, (Boolean) value.unwrapped());
                    break;
                case STRING:
                    sink.set(key, false, (String) value.unwrapped());
                    break;
                case NUMBER: {
                    Number n = (Number) value.unwrapped();
                    if (n.doubleValue() % 1 == 0)
                        sink.set(key, false, n.longValue());
                    else
                        sink.set(key, false, n.doubleValue());
                    break;
                }
                case LIST:
                    int index = 0;
                    for (ConfigValue cv : (ConfigList) value) {
                        entries.add(new AbstractMap.SimpleEntry<>(key + "[" + index + "]", cv));
                        ++index;
                    }
                    break;
                case OBJECT:
                    for(Map.Entry<String, ConfigValue> e : ((ConfigObject)value).entrySet())
                        entries.add(new AbstractMap.SimpleEntry<>(key + "." + e.getKey(), e.getValue()));
                    break;
                default: // NULL
                    sink.forceDefault(key);
                    break;
            }
        }
    }
}
