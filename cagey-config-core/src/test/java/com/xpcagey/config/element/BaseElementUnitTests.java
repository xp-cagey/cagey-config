package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static junit.framework.TestCase.*;

public class BaseElementUnitTests {
    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void reflectParameters() {
        BaseElement base = new BaseElement<Boolean>("x", true, true) {};
        assertTrue(base.isSensitive());
        assertEquals("x", base.getKey());
        assertTrue(base.hasRawValue(Boolean.TRUE));

        base = new BaseElement<Boolean>("y", false, true) {};
        assertFalse(base.isSensitive());
        assertEquals("y", base.getKey());
        assertTrue(base.hasRawValue(Boolean.TRUE));
    }

    @Test
    public void checkCompare() {
        BaseElement base = new BaseElement<Boolean>("x", true, true) {};
        assertTrue(base.isSensitive());
        assertEquals("x", base.getKey());

        // key order should dictate ordering
        assertEquals(0, base.compareTo(new BaseElement<Boolean>("x", true, true) {}));
        assertTrue(base.compareTo(new BaseElement<Boolean>("a", true, true) {}) > 0);
        assertTrue(base.compareTo(new BaseElement<Boolean>("z", true, true) {}) < 0);
    }

    @Test
    public void checkValueCompare() {
        BaseElement base = new BaseElement<Boolean>("x", true, true) {};
        assertTrue(base.isSensitive());
        assertEquals("x", base.getKey());

        assertTrue(base.hasRawValue(Boolean.TRUE));
        assertFalse(base.hasRawValue(null));
        assertFalse(base.hasRawValue(1L));

        assertFalse(base.hasEqualValue(null));
        assertFalse(base.hasEqualValue(new BaseElement<Long>("x", true, 1L) {}));
        assertTrue(base.hasEqualValue(new BaseElement<Boolean>("x", true, true) {}));
    }

    @Test
    public void forwardDefaults() {
        DefaultManagement.set("x", 123);

        Element base = new BaseElement<Boolean>("x", false, true) {};
        assertTrue(base.getAsBoolean());
        assertEquals(123, base.getAsDouble(), 0.00001);
        assertEquals(Duration.ofMillis(123), base.getAsDuration());
        assertEquals(Instant.ofEpochMilli(123), base.getAsInstant());
        assertEquals(123, base.getAsLong());
        assertEquals("123", base.getAsString());

        // automatically update with defaults
        DefaultManagement.set("x", false);
        assertFalse(base.getAsBoolean());
    }
}
