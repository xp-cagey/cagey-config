package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;

import java.util.concurrent.ConcurrentHashMap;

class OnInitialized extends MergeOperator {
    OnInitialized(ConcurrentHashMap<String, ImmutableSortedElementSet> map, ConfigEventBindings<Element> bindings) {
        super(map, bindings);
    }

    @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
        if (prev == null)
            return new ImmutableSortedElementSet(score, element);
        return prev.addOrIgnore(score, element);
    }
}