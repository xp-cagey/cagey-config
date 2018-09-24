package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;

public class SubtreeElementUnitTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void shouldForwardInnerValues() {
        RawValueElement ele = new StringElement("prefix.key", true, "1.01");
        Element wrapper = new SubtreeElement("key", ele);
        assertNotEquals(ele.getKey(), wrapper.getKey());
        assertEquals("key", wrapper.getKey());
        assertEquals(ele.isSensitive(), wrapper.isSensitive());
        assertEquals(ele.getAsBoolean(), wrapper.getAsBoolean());
        assertEquals(ele.getAsDouble(), wrapper.getAsDouble(), 0.000001);
        assertEquals(ele.getAsDuration(), wrapper.getAsDuration());
        assertEquals(ele.getAsInstant(), wrapper.getAsInstant());
        assertEquals(ele.getAsLong(), wrapper.getAsLong());
        assertEquals(ele.getAsString(), wrapper.getAsString());

        assertTrue(ele.hasEqualValue(wrapper));
        assertTrue(wrapper.hasEqualValue(ele));

        assertEquals(0, wrapper.compareTo(ele));
        assertEquals(0, ele.compareTo(wrapper));
    }

    @Test
    public void shouldEquateAtDifferentPrefixLevels() {
        RawValueElement ele = new StringElement("prefix.nested.key", true, "1.01");
        Element wrapper = new SubtreeElement("key", ele);
        Element wrapper2 = new SubtreeElement("nested.key", ele);
        assertNotEquals(wrapper.getKey(), wrapper2.getKey());

        assertTrue(wrapper.hasEqualValue(wrapper2));
        assertTrue(wrapper2.hasEqualValue(wrapper));

        assertEquals(0, wrapper.compareTo(wrapper2));
        assertEquals(0, wrapper2.compareTo(wrapper));
    }
}
