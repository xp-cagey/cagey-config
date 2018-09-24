package com.xpcagey.config.core;

import com.xpcagey.config.element.RawValueElement;

import java.util.concurrent.ConcurrentMap;

abstract class MergeOperator {
    private final ConcurrentMap<String, ImmutableSortedElementSet> map;
    private final ConfigEventBindings<RawValueElement> bindings;

    MergeOperator(ConcurrentMap<String, ImmutableSortedElementSet> map, ConfigEventBindings<RawValueElement> bindings) {
        this.map = map;
        this.bindings = bindings;
    }

    void apply(String key, int score, RawValueElement e) {
        ImmutableSortedElementSet prev, value;
        while (true) {
            prev = map.get(key);
            value = execute(prev, score, e);
            if (value == null) {
                if (prev == null || map.remove(key, prev))
                    break;
            } else if (prev == null) {
                if (map.putIfAbsent(key, value) == null)
                    break;
            } else if (map.replace(key, prev, value))
                break;
        }
        boolean changed = value == null ? prev != null : !value.matches(prev);
        if (changed) {
            if (value == null)
                bindings.notifyRemoved(key);
            else
                bindings.notify(value.element());
        }
    }

    protected abstract ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element);
}
