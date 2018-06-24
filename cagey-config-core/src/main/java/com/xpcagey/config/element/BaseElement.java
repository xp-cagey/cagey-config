package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;

import java.time.Duration;
import java.time.Instant;

abstract class BaseElement<T> implements RawValueElement {
    private final String key;
    private final boolean sensitive;
    protected final transient T value;

    BaseElement(String key, boolean sensitive, T value) {
        this.key = key;
        this.sensitive = sensitive;
        this.value = value;
    }

    @Override public boolean isSensitive() { return sensitive; }
    @Override public String getKey() { return key; }

    @Override public boolean getAsBoolean() { return DefaultManagement.getBoolean(key); }
    @Override public double getAsDouble() { return DefaultManagement.getDouble(key); }
    @Override public Duration getAsDuration() { return DefaultManagement.getDuration(key); }
    @Override public Instant getAsInstant() { return DefaultManagement.getInstant(key); }
    @Override public long getAsLong() { return DefaultManagement.getLong(key); }
    @Override public String getAsString() { return DefaultManagement.getString(key); }

    @Override public int compareTo(Element o) { return key.compareTo(o.getKey()); }
    @Override public boolean hasRawValue(Object value) { return value != null && value.equals(this.value); }
    @Override public boolean hasEqualValue(Element other) {
        if (other instanceof RawValueElement)
            return ((RawValueElement)other).hasRawValue(value);
        return false;
    }
}
