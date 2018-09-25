package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.*;

import static org.junit.Assert.*;

public class StringElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkToBooleanMatch() {
        Element def = new StringElement("x", false, "true");
        assertTrue(def.getAsBoolean());
        def = new StringElement("x", false, "false");
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkToBooleanMismatch() {
        DefaultManagement.set("x", true);

        Element def = new StringElement("x", false, "???");
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkToDoubleMatch() {
        Element def = new StringElement("x", false, "1234.567");
        assertEquals(1234.567, def.getAsDouble(), 0.000001);
        def = new StringElement("x", false, "NaN");
        assertEquals(Double.NaN, def.getAsDouble(), 0.000000);
    }

    @Test
    public void checkToDoubleMismatch() {
        DefaultManagement.set("x", 123.456);

        Element def = new StringElement("x", false,"not a number");
        assertEquals(123.456, def.getAsDouble(), 0.000001);

        DefaultManagement.set("x", 444.444);
        assertEquals(444.444, def.getAsDouble(), 0.000001);
    }

    @Test
    public void checkToDurationMatch() {
        Element def = new StringElement("x", false,"PT3M");
        assertEquals(Duration.ofMinutes(3), def.getAsDuration());
        def = new StringElement("x", false,"PT24S");
        assertEquals(Duration.ofSeconds(24), def.getAsDuration());
    }

    @Test
    public void checkToDurationMismatch() {
        DefaultManagement.set("x", Duration.ofMinutes(3));

        Element def = new StringElement("x", false,"???");
        assertEquals(Duration.ofMinutes(3), def.getAsDuration());

        DefaultManagement.set("x", Duration.ofHours(2));
        assertEquals(Duration.ofHours(2), def.getAsDuration());
    }

    @Test
    public void checkToInstantMatch() {
        Element def = new StringElement("x", false,"1999-12-31T23:59:59Z");
        LocalDateTime time = LocalDateTime.of(1999, 12, 31, 23, 59, 59);
        ZonedDateTime zoned = ZonedDateTime.of(time, ZoneOffset.UTC);
        assertEquals(zoned.toInstant(), def.getAsInstant());
    }

    @Test
    public void checkToInstantMismatch() {
        Instant now = Instant.now();
        DefaultManagement.set("x", now);

        Element def = new StringElement("x", false,"???");
        assertEquals(now, def.getAsInstant());
    }

    @Test
    public void checkToLongMatch() {
        Element def = new StringElement("x", false,"1234.567");
        assertEquals(1234, def.getAsLong()); // truncation
    }

    @Test
    public void checkToLongMismatch() {
        Element def = new StringElement("x", false,"not a number");
        assertEquals(0, def.getAsLong()); // truncation

        DefaultManagement.set("x", 123);
        assertEquals(123, def.getAsLong());
    }

    @Test
    public void checkToString() {
        Element def = new StringElement("x", false, "this is a value");
        assertEquals("this is a value", def.getAsString());

        DefaultManagement.set("x", 123);
        assertEquals("this is a value", def.getAsString());
    }
}
