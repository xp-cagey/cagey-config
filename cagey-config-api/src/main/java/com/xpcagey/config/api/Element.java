package com.xpcagey.config.api;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

/**
 * Each Element is a single value inside of configuration. A series of casting
 * functions is provided regardless of the source type; where casts are not
 * supported the zero or empty value should be returned.
 */
public interface Element extends Comparable<Element>, BooleanSupplier, DoubleSupplier, LongSupplier {
    /**
     * If an element is sensitive it should not be logged or stored without encryption
     * @return true if sensitive
     */
    boolean isSensitive();

    /**
     * The key for this element relative to the root of its configuration
     * @return the key
     */
    String getKey();

    Duration getAsDuration();
    Instant getAsInstant();
    String getAsString();

    boolean hasEqualValue(Element other);
}
