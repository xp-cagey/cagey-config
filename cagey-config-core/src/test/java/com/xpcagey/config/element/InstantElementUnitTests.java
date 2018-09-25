package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class InstantElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToDouble() {
        Element def = new InstantElement("x", false, Instant.ofEpochMilli(123));
        assertEquals(123, def.getAsDouble(), 0.000001);
        def = new InstantElement("x", false, Instant.ofEpochMilli(987654));
        assertEquals(987654, def.getAsDouble(), 0.000000);
    }

    @Test
    public void checkToInstant() {
        Element def = new InstantElement("x", false, Instant.ofEpochMilli(987654));
        assertEquals(Instant.ofEpochMilli(987654), def.getAsInstant());
    }

    @Test
    public void checkToLong() {
        Element def = new InstantElement("x", false, Instant.ofEpochMilli(987654));
        assertEquals(987654, def.getAsLong()); // truncation
    }

    @Test
    public void checkToString() {
        Element def = new InstantElement("x", false, Instant.ofEpochMilli(987654));
        assertEquals("1970-01-01T00:16:27.654Z", def.getAsString());
    }
}
