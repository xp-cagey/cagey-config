package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

class ConfigEventBindings<T extends Element> {
    private final ConcurrentHashMap<String, WeakConsumers<T>> triggers = new ConcurrentHashMap<>();
    private final WeakConsumers<String> reapers = new WeakConsumers<>(false);
    private final WeakConsumers<Boolean> dependencies = new WeakConsumers<>(false);
    private final WeakConsumers<T> listeners = new WeakConsumers<>(false);
    private final Executor exec;

    ConfigEventBindings(Executor exec) {
        this.exec = exec;
    }

    void addReaper(Consumer<String> listener) { reapers.add(listener); }
    void removeReaper(Consumer<String> listener) { reapers.remove(listener); }

    void notifyRemoved(String key) { reapers.test(exec, key); }

    void addListener(Consumer<? super T> listener) { listeners.add(listener); }
    void removeListener(Consumer<? super T> listener) { listeners.remove(listener); }

    void addTrigger(String key, Consumer<? super T> trigger) {
        for (;;) {
            WeakConsumers<T> values = triggers.computeIfAbsent(key, k -> new WeakConsumers<>(true));
            if (values.add(trigger))
                break;
            triggers.remove(key, values);
        }
    }

    void removeTrigger(String key, Consumer<? super T> trigger) {
        WeakConsumers<T> values = triggers.getOrDefault(key, null);
        if (values != null) {
            if (values.remove(trigger))
                triggers.remove(key, values);
        }
    }

    void notify(T e) {
        listeners.test(exec, e);
        WeakConsumers<T> values = triggers.getOrDefault(e.getKey(), null);
        if (values != null) {
            if (!values.test(exec, e))
                triggers.remove(e.getKey(), values);
        }
    }

    void addDependency(Consumer<Boolean> dependency) { dependencies.add(dependency); }
    void removeDependency(Consumer<Boolean> dependency) { dependencies.remove(dependency); }
    void notifyClosed() { dependencies.test(exec, true); }

    // package access for unit testing only
    void clear() {
        reapers.clear();
        listeners.clear();
        dependencies.clear();
        triggers.forEach((k, t) -> t.clear());
   }
}
