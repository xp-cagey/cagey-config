package com.xpcagey.config.element;

import java.time.Instant;

public class InstantElement extends BaseElement<Instant> {
    public InstantElement(String key, boolean sensitive, Instant value) {
        super(key, sensitive, value);
    }

    @Override public double getAsDouble() { return value.toEpochMilli(); }
    @Override public Instant getAsInstant() { return value; }
    @Override public long getAsLong() { return value.toEpochMilli(); }
    @Override public String getAsString() { return value.toString(); }
}
