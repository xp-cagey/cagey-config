package com.xpcagey.config.element;

import java.time.Duration;
import java.time.Instant;

public class DoubleElement extends BaseElement<Double> {
    public DoubleElement(String key, boolean sensitive, double value) {
        super(key, sensitive, value);
    }

    @Override public boolean getAsBoolean() { return value != 0.0; }
    @Override public double getAsDouble() { return value; }
    @Override public Duration getAsDuration() { return Duration.ofMillis(value.longValue()); }
    @Override public Instant getAsInstant() { return Instant.ofEpochMilli(value.longValue()); }
    @Override public long getAsLong() { return value.longValue(); }
    @Override public String getAsString() { return value.toString(); }
}
