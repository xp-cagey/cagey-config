package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class DurationElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToDouble() {
        Element def = new DurationElement("x", false, Duration.ofSeconds(23));
        assertEquals(23000, def.getAsDouble(), 0.000001);
        def = new DurationElement("x", false, Duration.ofMillis(-1));
        assertEquals(-1.0, def.getAsDouble(), 0.0000001);
    }

    @Test
    public void checkToDuration() {
        Element def = new DurationElement("x", false,Duration.ofMillis(123));
        assertEquals(Duration.ofMillis(123), def.getAsDuration());
    }

    @Test
    public void checkToLong() {
        Element def = new DurationElement("x", false, Duration.ofSeconds(23));
        assertEquals(23000, def.getAsLong());
        def = new DurationElement("x", false, Duration.ofMillis(-1));
        assertEquals(-1, def.getAsLong());
    }

    @Test
    public void checkToString() {
        Element def = new DurationElement("x", false, Duration.ofSeconds(23));
        assertEquals("PT23S", def.getAsString());
    }
}
