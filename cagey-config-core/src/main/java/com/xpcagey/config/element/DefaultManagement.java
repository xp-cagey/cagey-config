package com.xpcagey.config.element;

import java.time.Duration;
import java.time.Instant;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class DefaultManagement {
    private static final SortedMap<String, DefaultElement> defaults = new ConcurrentSkipListMap<>();
    private static final DefaultElement fallback = new DefaultElement("", "");

    private DefaultManagement() {}

    public static void set(String key, boolean value) { defaults.put(key, new DefaultElement(key, value)); }
    public static void set(String key, double value) { defaults.put(key, new DefaultElement(key, value)); }
    public static void set(String key, Duration value) { defaults.put(key, new DefaultElement(key, value)); }
    public static void set(String key, Instant value) { defaults.put(key, new DefaultElement(key, value)); }
    public static void set(String key, long value) { defaults.put(key, new DefaultElement(key, value)); }
    public static void set(String key, String value) { defaults.put(key, new DefaultElement(key, value)); }
    public static DefaultElement getOrNull(String key) { return defaults.get(key); }

    static boolean getBoolean(String key) { return defaults.getOrDefault(key, fallback).getAsBoolean(); }
    static double getDouble(String key) { return defaults.getOrDefault(key, fallback).getAsDouble(); }
    static Duration getDuration(String key) { return defaults.getOrDefault(key, fallback).getAsDuration(); }
    static Instant getInstant(String key) { return defaults.getOrDefault(key, fallback).getAsInstant(); }
    static long getLong(String key) {
        return defaults.getOrDefault(key, fallback).getAsLong();
    }
    static String getString(String key) { return defaults.getOrDefault(key, fallback).getAsString(); }
    static DefaultElement get(String key) { return defaults.getOrDefault(key, fallback); }

    // for unit test use only
    public static void reset() { defaults.clear(); }
}

