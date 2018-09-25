package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class DoubleElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToBoolean() {
        Element def = new DoubleElement("x", false, 1.3);
        assertTrue(def.getAsBoolean());
        def = new DoubleElement("x", false, 0.0);
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkToDouble() {
        Element def = new DoubleElement("x", false, 123.999);
        assertEquals(123.999, def.getAsDouble(), 0.000001);
        def = new DoubleElement("x", false, 987654);
        assertEquals(987654, def.getAsDouble(), 0.000000);
    }

    @Test
    public void checkToDuration() {
        Element def = new DoubleElement("x", false,123.999);
        assertEquals(Duration.ofMillis(123), def.getAsDuration());
    }

    @Test
    public void checkToInstant() {
        Element def = new DoubleElement("x", false,123.999);
        assertEquals(Instant.ofEpochMilli(123), def.getAsInstant());
    }

    @Test
    public void checkToLong() {
        Element def = new DoubleElement("x", false,123.999);
        assertEquals(123, def.getAsLong()); // truncation
    }

    @Test
    public void checkToString() {
        Element def = new DoubleElement("x", false, 123.999);
        assertEquals("123.999", def.getAsString());
    }
}
