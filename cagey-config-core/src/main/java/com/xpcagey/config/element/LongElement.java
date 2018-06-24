package com.xpcagey.config.element;

import java.time.Duration;
import java.time.Instant;

public class LongElement extends BaseElement<Long> {
    public LongElement(String key, boolean sensitive, long value) {
        super(key, sensitive, value);
    }

    @Override public boolean getAsBoolean() { return value != 0; }
    @Override public double getAsDouble() { return value; }
    @Override public Duration getAsDuration() { return Duration.ofMillis(value); }
    @Override public Instant getAsInstant() { return Instant.ofEpochMilli(value); }
    @Override public long getAsLong() { return value; }
    @Override public String getAsString() { return Long.valueOf(value).toString(); }
}
