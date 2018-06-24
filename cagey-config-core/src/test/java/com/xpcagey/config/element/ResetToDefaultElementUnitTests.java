package com.xpcagey.config.element;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResetToDefaultElementUnitTests {

    @Before
    public void setup() {
        DefaultManagement.reset();
    }

    @Test
    public void checkUsesDefault() {
        DefaultManagement.set("key-x", 123);
        assertEquals(123, new ResetToDefaultElement("key-x").getAsLong());
        assertTrue(new ResetToDefaultElement("key-x").hasRawValue(123L));
        assertTrue(new ResetToDefaultElement("key-x").hasEqualValue(DefaultManagement.get("key-x")));
        assertTrue(new ResetToDefaultElement("key-x").hasEqualValue(new ResetToDefaultElement("key-x")));
        assertFalse(new ResetToDefaultElement("key-x").hasEqualValue(new ResetToDefaultElement("key-y")));
        assertFalse(new ResetToDefaultElement("key-x").hasEqualValue(null));
    }
}
