package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class LongElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToBoolean() {
        Element def = new LongElement("x", false, 1);
        assertTrue(def.getAsBoolean());
        def = new LongElement("x", false, 0);
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkToDouble() {
        Element def = new LongElement("x", false, 123);
        assertEquals(123.0, def.getAsDouble(), 0.000001);
        def = new LongElement("x", false, 987654);
        assertEquals(987654, def.getAsDouble(), 0.000000);
    }

    @Test
    public void checkToDuration() {
        Element def = new LongElement("x", false,123);
        assertEquals(Duration.ofMillis(123), def.getAsDuration());
    }

    @Test
    public void checkToInstant() {
        Element def = new LongElement("x", false,123);
        assertEquals(Instant.ofEpochMilli(123), def.getAsInstant());
    }

    @Test
    public void checkToLong() {
        Element def = new LongElement("x", false,123);
        assertEquals(123, def.getAsLong()); // truncation
    }

    @Test
    public void checkToString() {
        Element def = new LongElement("x", false, 123);
        assertEquals("123", def.getAsString());
    }
}
