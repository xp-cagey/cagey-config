package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class DefaultElement implements RawValueElement {
    private final Object rawValue;
    private final boolean booleanValue;
    private final String stringValue;
    private final double doubleValue;
    private final Duration durationValue;
    private final Instant instantValue;
    private final long longValue;
    private final String key;

    public DefaultElement(String key, boolean value) {
        this.key = key;
        rawValue = value;
        booleanValue = value;
        stringValue = Boolean.valueOf(value).toString();
        doubleValue = value ? 1.0 : 0.0;
        durationValue = Duration.ZERO;
        instantValue = Instant.EPOCH;
        longValue = value ? 1L : 0L;
    }

    public DefaultElement(String key, double value) {
        Double dbl = value;
        this.key = key;
        rawValue = value;
        booleanValue = value != 0.0;
        stringValue = dbl.toString();
        doubleValue = value;
        durationValue = Duration.ofMillis(dbl.longValue());
        instantValue = Instant.ofEpochMilli(dbl.longValue());
        longValue = dbl.longValue();
    }

    public DefaultElement(String key, Duration value) {
        this.key = key;
        rawValue = value;
        booleanValue = false;
        stringValue = value.toString();
        doubleValue = value.toMillis();
        durationValue = value;
        instantValue = Instant.EPOCH;
        longValue = value.toMillis();
    }

    public DefaultElement(String key, Instant value) {
        this.key = key;
        rawValue = value;
        booleanValue = false;
        stringValue = value.toString();
        doubleValue = value.toEpochMilli();
        durationValue = Duration.ZERO;
        instantValue = value;
        longValue = value.toEpochMilli();
    }

    public DefaultElement(String key, long value) {
        Long lng = value;
        this.key = key;
        rawValue = value;
        booleanValue = value != 0L;
        stringValue = lng.toString();
        doubleValue = value;
        durationValue = Duration.ofMillis(value);
        instantValue = Instant.ofEpochMilli(value);
        longValue = value;
    }

    public DefaultElement(String key, String value) {
        this.key = key;
        rawValue = value;
        booleanValue = Boolean.valueOf(value);
        stringValue = value;

        double dValue = 0.0;
        try {
            dValue = Double.valueOf(value);
        } catch (NumberFormatException e) { /* */ }
        doubleValue = dValue;

        Duration duValue = Duration.ZERO;
        try {
            duValue = Duration.parse(value);
        } catch (DateTimeParseException e) { /* */ }
        durationValue = duValue;

        Instant iValue = Instant.EPOCH;
        try {
            iValue = Instant.parse(value);
        } catch (DateTimeParseException e) { /* */ }
        instantValue = iValue;

        long lValue = 0L;
        try {
            lValue = Double.valueOf(value).longValue();
        } catch (NumberFormatException e) { /* */ }
        longValue = lValue;
    }

    @Override public boolean isSensitive() { return false; }
    @Override public String getKey() { return key; }
    @Override public boolean getAsBoolean() { return booleanValue; }
    @Override public double getAsDouble() { return doubleValue; }
    @Override public Duration getAsDuration() { return durationValue; }
    @Override public Instant getAsInstant() { return instantValue; }
    @Override public long getAsLong() { return longValue; }
    @Override public String getAsString() { return stringValue; }
    @Override public int compareTo(Element o) { return key.compareTo(o.getKey()); }
    @Override public boolean hasRawValue(Object value) { return value != null && value.equals(this.rawValue); }
    @Override public boolean hasEqualValue(Element other) {
        if (other instanceof RawValueElement)
            return ((RawValueElement)other).hasRawValue(rawValue);
        return false;
    }
}

