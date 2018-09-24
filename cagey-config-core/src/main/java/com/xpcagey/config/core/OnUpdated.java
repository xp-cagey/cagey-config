package com.xpcagey.config.core;

import com.xpcagey.config.element.RawValueElement;

import java.util.concurrent.ConcurrentHashMap;

class OnUpdated extends MergeOperator {
    OnUpdated(ConcurrentHashMap<String, ImmutableSortedElementSet> map, ConfigEventBindings<RawValueElement> bindings) {
        super(map, bindings);
    }

    @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
        if (prev == null)
            return new ImmutableSortedElementSet(score, element);
        return prev.addOrReplace(score, element);
    }
}
