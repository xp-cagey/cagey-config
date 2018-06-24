package com.xpcagey.config.element;

import java.time.Duration;

public class DurationElement extends BaseElement<Duration> {
    public DurationElement(String key, boolean sensitive, Duration value) {
        super(key, sensitive, value);
    }

    @Override public double getAsDouble() { return value.toMillis(); }
    @Override public Duration getAsDuration() { return value; }
    @Override public long getAsLong() { return value.toMillis(); }
    @Override public String getAsString() { return value.toString(); }
}
