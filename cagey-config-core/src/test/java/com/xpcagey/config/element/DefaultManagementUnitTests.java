package com.xpcagey.config.element;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class DefaultManagementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void shouldReturnFallbackValuesCorrectly() {
        assertFalse(DefaultManagement.getBoolean("boolean-x"));
        assertEquals(0.0, DefaultManagement.getDouble("double-x"), 0.0);
        assertEquals(Duration.ZERO, DefaultManagement.getDuration("duration-x"));
        assertEquals(Instant.EPOCH, DefaultManagement.getInstant("instant-x"));
        assertEquals(0L, DefaultManagement.getLong("long-x"));
        assertEquals("", DefaultManagement.getString("string-x"));
    }

    @Test
    public void shouldReturnMatchedValuesCorrectly() {
        DefaultManagement.set("boolean-x", true);
        DefaultManagement.set("double-x", 123.456);
        DefaultManagement.set("duration-x", Duration.ofHours(123));
        DefaultManagement.set("instant-x", Instant.ofEpochMilli(987654321987654321L));
        DefaultManagement.set("long-x", 1234567890987654321L);
        DefaultManagement.set("string-x", "Hello world!");

        assertTrue(DefaultManagement.getBoolean("boolean-x"));
        assertEquals(123.456, DefaultManagement.getDouble("double-x"), 0.000001);
        assertEquals(Duration.ofHours(123), DefaultManagement.getDuration("duration-x"));
        assertEquals(Instant.ofEpochMilli(987654321987654321L), DefaultManagement.getInstant("instant-x"));
        assertEquals(1234567890987654321L, DefaultManagement.getLong("long-x"));
        assertEquals("Hello world!", DefaultManagement.getString("string-x"));
    }

    @Test
    public void shouldOverwriteValuesCorrectly() {
        DefaultManagement.set("key-x", 12);
        assertEquals("12", DefaultManagement.getString("key-x"));
        DefaultManagement.set("key-x", "other");
        assertEquals("other", DefaultManagement.getString("key-x"));
    }

    @Test
    public void shouldReportUnsetValuesCorrectly() {
        DefaultManagement.set("key-x", 12);
        assertEquals("12", DefaultManagement.getOrNull("key-x").getAsString());
        assertNull(DefaultManagement.getOrNull("key-2-x"));
    }}
