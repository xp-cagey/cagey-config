package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;

import java.time.Duration;
import java.time.Instant;

public class SubtreeElement implements RawValueElement {
    private final String key;
    private final RawValueElement src;

    public SubtreeElement(String key, RawValueElement src) {
        this.key = key;
        this.src = src;
    }

    @Override public boolean hasRawValue(Object value) { return src.hasRawValue(value); }
    @Override public boolean isSensitive() { return src.isSensitive(); }
    @Override public String getKey() { return key; }
    @Override public Duration getAsDuration() { return src.getAsDuration(); }
    @Override public Instant getAsInstant() { return src.getAsInstant(); }
    @Override public String getAsString() { return src.getAsString(); }
    @Override public boolean hasEqualValue(Element other) { return src.hasEqualValue(other); }
    @Override public boolean getAsBoolean() { return src.getAsBoolean(); }
    @Override public double getAsDouble() { return src.getAsDouble(); }
    @Override public long getAsLong() { return src.getAsLong(); }
    @Override public int compareTo(Element other) { return other.compareTo(src); }
}
