package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Included to allow mocking without unchecked warnings.
 */
interface BooleanConsumer extends Consumer<Boolean> {}
interface ElementConsumer extends Consumer<Element> {}
interface RawValueElementConsumer extends Consumer<RawValueElement> {}
interface StringConsumer extends Consumer<String> {}
interface ElementConcurrentMap extends ConcurrentMap<String, ImmutableSortedElementSet> {}

class RawValueElementConfigEventBindings extends ConfigEventBindings<RawValueElement> {
    RawValueElementConfigEventBindings(Executor exec) {
        super(exec);
    }
}


