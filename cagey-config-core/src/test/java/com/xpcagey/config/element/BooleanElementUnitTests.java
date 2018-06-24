package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class BooleanElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToBoolean() {
        Element def = new BooleanElement("x", false, true);
        assertTrue(def.getAsBoolean());
        def = new BooleanElement("x", false, false);
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkToDouble() {
        Element def = new BooleanElement("x", false, true);
        assertEquals(1.0, def.getAsDouble(), 0.0);
        def = new BooleanElement("x", false, false);
        assertEquals(0.0, def.getAsDouble(), 0.0);
    }

    @Test
    public void checkToLong() {
        Element def = new BooleanElement("x", false, true);
        assertEquals(1, def.getAsLong());
        def = new BooleanElement("x", false, false);
        assertEquals(0, def.getAsLong());
    }

    @Test
    public void checkToString() {
        Element def = new BooleanElement("x", false, true);
        assertEquals("true", def.getAsString());
        def = new BooleanElement("x", false, false);
        assertEquals("false", def.getAsString());
    }
}
