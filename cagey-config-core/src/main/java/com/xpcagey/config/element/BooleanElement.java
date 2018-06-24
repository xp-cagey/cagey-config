package com.xpcagey.config.element;

public class BooleanElement extends BaseElement<Boolean> {
    public BooleanElement(String key, boolean sensitive, boolean value) {
        super(key, sensitive, value);
    }
    @Override public boolean getAsBoolean() { return value; }
    @Override public double getAsDouble() { return value ? 1.0 : 0.0; }
    @Override public long getAsLong() { return value ? 1L : 0L; }
    @Override public String getAsString() { return Boolean.toString(value); }
}
