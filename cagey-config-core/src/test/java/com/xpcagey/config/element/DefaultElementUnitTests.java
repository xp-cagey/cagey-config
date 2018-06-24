package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Test;

import java.time.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultElementUnitTests {
    @Test
    public void checkSensitive() {
        Element def = new DefaultElement("x", false);
        assertFalse(def.isSensitive());
    }

    @Test
    public void checkCompareTo() {
        Element def = new DefaultElement("x", false);
        Element def2 = new DefaultElement("x", "some other value");
        assertEquals(0, def.compareTo(def2));

        def2 = new DefaultElement("y", false);
        assertEquals(-1, def.compareTo(def2));
    }

    @Test
    public void checkGetKey() {
        Element def = new DefaultElement("x", false);
        assertEquals("x", def.getKey());
    }

    @Test
    public void checkBooleanToDouble() {
        Element def = new DefaultElement("x", false);
        assertEquals(0, def.getAsDouble(), 0);
        def = new DefaultElement("x", true);
        assertEquals(1, def.getAsDouble(), 0);
    }

    @Test
    public void checkBooleanToDuration() {
        Element def = new DefaultElement("x", false);
        assertEquals(Duration.ZERO, def.getAsDuration());
        def = new DefaultElement("x", true);
        assertEquals(Duration.ZERO, def.getAsDuration());
    }

    @Test
    public void checkBooleanToInstant() {
        Element def = new DefaultElement("x", false);
        assertEquals(Instant.EPOCH, def.getAsInstant());
        def = new DefaultElement("x", true);
        assertEquals(Instant.EPOCH, def.getAsInstant());
    }

    @Test
    public void checkBooleanToLong() {
        Element def = new DefaultElement("x", false);
        assertEquals(0, def.getAsLong());
        def = new DefaultElement("x", true);
        assertEquals(1, def.getAsLong());
    }

    @Test
    public void checkBooleanToString() {
        Element def = new DefaultElement("x", false);
        assertEquals("false", def.getAsString());
        def = new DefaultElement("x", true);
        assertEquals("true", def.getAsString());
    }

    @Test
    public void checkDoubleToBoolean() {
        Element def = new DefaultElement("x", 123.456);
        assertTrue(def.getAsBoolean());
        def = new DefaultElement("x", 0.0);
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkDoubleToDuration() {
        Element def = new DefaultElement("x", 123.456);
        assertEquals(Duration.ofMillis(123), def.getAsDuration());
    }

    @Test
    public void checkDoubleToInstant() {
        Element def = new DefaultElement("x", 123.456);
        assertEquals(Instant.ofEpochMilli(123), def.getAsInstant());
    }

    @Test
    public void checkDoubleToLong() {
        Element def = new DefaultElement("x", 123.999);
        assertEquals(123, def.getAsLong()); // confirm truncation
    }

    @Test
    public void checkDoubleToString() {
        Element def = new DefaultElement("x", 123.456);
        assertEquals("123.456", def.getAsString());
    }

    @Test
    public void checkDurationToBoolean() {
        Element def = new DefaultElement("x", Duration.ofSeconds(1));
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkDurationToDouble() {
        Element def = new DefaultElement("x", Duration.ofSeconds(1));
        assertEquals(1000.0, def.getAsDouble(), 0);
    }

    @Test
    public void checkDurationToInstant() {
        Element def = new DefaultElement("x", Duration.ofSeconds(1));
        assertEquals(Instant.EPOCH, def.getAsInstant());
    }

    @Test
    public void checkDurationToLong() {
        Element def = new DefaultElement("x", Duration.ofSeconds(1));
        assertEquals(1000, def.getAsLong());
    }

    @Test
    public void checkDurationToString() {
        Element def = new DefaultElement("x", Duration.ofMinutes(8));
        assertEquals("PT8M", def.getAsString());
    }

    @Test
    public void checkInstantToBoolean() {
        Element def = new DefaultElement("x", Instant.ofEpochMilli(8));
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkInstantToDouble() {
        Element def = new DefaultElement("x", Instant.ofEpochSecond(1));
        assertEquals(1000.0, def.getAsDouble(), 0);
    }

    @Test
    public void checkInstantToLong() {
        Element def = new DefaultElement("x", Instant.ofEpochSecond(1));
        assertEquals(1000, def.getAsLong());
    }

    @Test
    public void checkInstantToString() {
        Element def = new DefaultElement("x", Instant.ofEpochSecond(10000000));
        assertEquals("1970-04-26T17:46:40Z", def.getAsString());
    }

    @Test
    public void checkLongToBoolean() {
        Element def = new DefaultElement("x", 123L);
        assertTrue(def.getAsBoolean());
        def = new DefaultElement("x", 0L);
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkLongToDouble() {
        Element def = new DefaultElement("x", 123L);
        assertEquals(123.0, def.getAsDouble(), 0.000001);
    }

    @Test
    public void checkLongToDuration() {
        Element def = new DefaultElement("x", 123L);
        assertEquals(Duration.ofMillis(123), def.getAsDuration());
    }

    @Test
    public void checkLongToInstant() {
        Element def = new DefaultElement("x", 123L);
        assertEquals(Instant.ofEpochMilli(123), def.getAsInstant());
    }

    @Test
    public void checkLongToString() {
        Element def = new DefaultElement("x", 123L);
        assertEquals("123", def.getAsString());
    }

    @Test
    public void checkStringToBoolean() {
        Element def = new DefaultElement("x", "true");
        assertTrue(def.getAsBoolean());
        def = new DefaultElement("x", "???");
        assertFalse(def.getAsBoolean());
        def = new DefaultElement("x", "false");
        assertFalse(def.getAsBoolean());
    }

    @Test
    public void checkStringToDuration() {
        Element def = new DefaultElement("x", "PT3M");
        assertEquals(Duration.ofMinutes(3), def.getAsDuration());
        def = new DefaultElement("x", "???");
        assertEquals(Duration.ZERO, def.getAsDuration());
    }

    @Test
    public void checkStringToInstant() {
        Element def = new DefaultElement("x", "1999-12-31T23:59:59Z");
        LocalDateTime time = LocalDateTime.of(1999, 12, 31, 23, 59, 59);
        ZonedDateTime zoned = ZonedDateTime.of(time, ZoneOffset.UTC);
        assertEquals(zoned.toInstant(), def.getAsInstant());
        def = new DefaultElement("x", "???");
        assertEquals(Instant.EPOCH, def.getAsInstant());
    }

    @Test
    public void checkStringToNumeric() {
        Element def = new DefaultElement("x", "1234.567");
        assertEquals(1234.567, def.getAsDouble(), 0.000001);
        assertEquals(1234, def.getAsLong()); // truncation

        def = new DefaultElement("x", "not a number");
        assertEquals(0, def.getAsDouble(), 0.000001);
        assertEquals(0, def.getAsLong()); // truncation
    }

    @Test
    public void checkHasRawValue() {
        DefaultElement def = new DefaultElement("x", "1234.567");
        assertTrue(def.hasRawValue("1234.567"));
        assertFalse(def.hasRawValue(null));
        assertFalse(def.hasRawValue(1234.567));
    }

    @Test
    public void checkHasEqualValue() {
        DefaultElement def = new DefaultElement("x", "1234.567");
        assertFalse(def.hasEqualValue(null));
        assertTrue(def.hasEqualValue(new BaseElement<String>("x", true, "1234.567") {}));
        assertFalse(def.hasEqualValue(new BaseElement<Double>("x", true, 1234.567) {}));
    }
}
