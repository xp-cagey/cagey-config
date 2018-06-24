package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;

import java.util.concurrent.ConcurrentHashMap;

class OnRemoved extends MergeOperator {
    OnRemoved(ConcurrentHashMap<String, ImmutableSortedElementSet> map, ConfigEventBindings<Element> bindings) {
        super(map, bindings);
    }

    @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
        if (prev == null)
            return null;
        return prev.remove(score);
    }
}