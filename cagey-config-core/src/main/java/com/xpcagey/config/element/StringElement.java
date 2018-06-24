package com.xpcagey.config.element;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

// TODO : cache conversions
public class StringElement extends BaseElement<String> {
    public StringElement(String key, boolean sensitive, String value) {
        super(key, sensitive, value);
    }

    @Override public boolean getAsBoolean() { return Boolean.valueOf(value); }
    @Override public String getAsString() { return value; }

    @Override public double getAsDouble() {
        try {
            return Double.valueOf(value);
        } catch(NumberFormatException e) {
            return super.getAsDouble();
        }
    }

    @Override public Duration getAsDuration() {
        try {
            return Duration.parse(value);
        } catch(DateTimeParseException e) {
            return super.getAsDuration();
        }
    }

    @Override public Instant getAsInstant() {
        try {
            return Instant.parse(value);
        } catch(DateTimeParseException e) {
            return super.getAsInstant();
        }
    }

    @Override public long getAsLong() {
        try {
            return Double.valueOf(value).longValue();
        } catch(NumberFormatException e) {
            return super.getAsLong();
        }
    }
}
