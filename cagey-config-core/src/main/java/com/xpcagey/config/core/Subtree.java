package com.xpcagey.config.core;

import com.xpcagey.config.api.Config;
import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;
import com.xpcagey.config.element.SubtreeElement;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class Subtree implements Config {
    private final ModularConfig root;
    private final String prefix;
    private ConfigEventBindings<RawValueElement> bindings;

    Subtree(ModularConfig root, Executor exec, String prefix) {
        if (!prefix.endsWith("."))
            prefix = prefix + ".";
        this.prefix = prefix;
        this.root = root;
        this.bindings = new ConfigEventBindings<>(exec);
        root.attach(this::onUpdated, this::onRootClosed);
    }

    @Override public String getName() { return prefix+"@"+root.getName(); }
    @Override public Iterator<String> getSources() { return root.getSources(); }
    @Override public SortedMap<String, Element> getAll() {
        final SortedMap<String, Element> out = new TreeMap<>();
        for (Map.Entry<String, ImmutableSortedElementSet> entry : root.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
                out.put(key, new SubtreeElement(key, entry.getValue().element()));
            }
        }
        return out;
    }

    @Override public Config subtree(String prefix) { return root.subtree(this.prefix + prefix); }

    @Override public Element getOrNull(String key) {
        RawValueElement item = root.getOrNull(prefix + key);
        if (item != null)
            return new SubtreeElement(key, item);
        return null;
    }

    @Override public void addListener(Consumer<Element> listener) { bindings.addListener(listener); }
    @Override public void removeListener(Consumer<Element> listener) { bindings.removeListener(listener); }
    @Override public void removeTrigger(String key, Consumer<Element> trigger) { bindings.removeTrigger(key, trigger); }
    @Override public void addTrigger(String key, Consumer<Element> trigger) {
        bindings.addTrigger(key, trigger);
        Element e = getOrNull(key);
        if (e != null)
            trigger.accept(e);
    }

    @Override public void close() {
        root.detatch(this::onUpdated, this::onRootClosed);
        bindings.clear();
    }
    @Override public Iterator<Element> iterator() { return getAll().values().iterator(); }

    private void onUpdated(RawValueElement e) {
        String key = e.getKey();
        if (!key.startsWith(prefix))
            return;
        bindings.notify(new SubtreeElement(key.substring(prefix.length()), e));
    }

    private void onRootClosed(Boolean b) {
        if (b) close();
    }
}
