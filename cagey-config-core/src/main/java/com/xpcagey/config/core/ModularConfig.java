package com.xpcagey.config.core;

import com.xpcagey.config.api.Config;
import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.DefaultManagement;
import com.xpcagey.config.element.RawValueElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ModularConfig implements Config {
    private final ConcurrentHashMap<String, ImmutableSortedElementSet> values = new ConcurrentHashMap<>();
    private final List<ConfigSettings> settings = new ArrayList<>();
    private final String name;
    private final Executor exec;
    private final ConfigEventBindings<RawValueElement> bindings;
    private final MergeOperator onInitialized;
    private final MergeOperator onUpdated;
    private final MergeOperator onRemoved;

    ModularConfig(Executor exec, String name) {
        this.name = name;
        this.exec = exec;
        this.bindings = new ConfigEventBindings<>(exec);
        this.onInitialized = new OnInitialized(values, bindings);
        this.onUpdated = new OnUpdated(values, bindings);
        this.onRemoved = new OnRemoved(values, bindings);
    }

    @Override public String getName() { return name; }
    @Override public Iterator<String> getSources() {
        Iterator<ConfigSettings> it = settings.iterator();
        return new Iterator<String>() {
            @Override public boolean hasNext() { return it.hasNext(); }
            @Override public String next() {
                ConfigModule module = it.next().module;
                return module.getProvider()+":"+module.getPath();
            }
        };
    }

    @Override public Config subtree(String prefix) { return new Subtree(this, exec, prefix); }

    @Override public SortedMap<String, Element> getAll() {
        final SortedMap<String, Element> out = new TreeMap<>();
        for (Map.Entry<String, ImmutableSortedElementSet> entry : entrySet()) {
            out.put(entry.getKey(), entry.getValue().element());
        }
        return out;
    }
    @Override public boolean hasKey(String key) { return values.containsKey(key); }
    @Override public RawValueElement getOrNull(String key) {
        final ImmutableSortedElementSet set = values.get(key);
        return (set == null) ? DefaultManagement.getOrNull(key) : set.element();
    }
    @Override public Iterator<Element> iterator() { return new ImmutableSortedElementSet.Iterator(values.values().iterator()); }
    @Override public void addListener(Consumer<Element> listener) { bindings.addListener(listener); }
    @Override public void removeListener(Consumer<Element> listener) { bindings.addListener(listener); }
    @Override public void removeTrigger(String key, Consumer<Element> trigger) { bindings.addTrigger(key, trigger); }
    @Override public void addTrigger(String key, Consumer<Element> trigger) {
        bindings.addTrigger(key, trigger);
        Element e = getOrNull(key);
        if (e != null)
            trigger.accept(e);
    }

    @Override public void close() {
        synchronized (settings) {
            for (ConfigSettings s : settings) {
                s.module.removeListener(s.listener);
                s.module.removeReaper(s.reaper);
            }
            settings.clear();
        }
        bindings.notifyClosed();
    }

    void append(ConfigModule module) {
        synchronized (settings) {
            final int score = settings.size();
            final ConfigSettings c = new ConfigSettings(
                    module,
                    element -> onUpdated.apply(element.getKey(), score, element),
                    key -> onRemoved.apply(key, score, null)
            );
            settings.add(c);
            module.addListener(c.listener);
            module.addReaper(c.reaper);

            for (RawValueElement e : module)
                onInitialized.apply(e.getKey(), score, e);
        }
    }



    void attach(Consumer<RawValueElement> listener, Consumer<Boolean> dependency) {
        bindings.addListener(listener);
        bindings.addDependency(dependency);
    }
    void detach(Consumer<RawValueElement> listener, Consumer<Boolean> dependency) {
        bindings.removeListener(listener);
        bindings.removeDependency(dependency);
    }

    Set<Map.Entry<String, ImmutableSortedElementSet>> entrySet() { return values.entrySet(); }

    private static class ConfigSettings {
        final ConfigModule module;
        final Consumer<RawValueElement> listener;
        final Consumer<String> reaper;
        ConfigSettings(ConfigModule module, Consumer<RawValueElement> listener, Consumer<String> reaper) {
            this.module = module;
            this.listener = listener;
            this.reaper = reaper;
        }
    }
}
