package com.xpcagey.config.api;

import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class DescriptorUnitTests {
    @Test
    public void shouldConstructProperly() throws URISyntaxException {
        Descriptor test = TestDescriptor.create("provider-x", "alias-x", "path", true);
        assertEquals("provider-x", test.getProvider());
        assertEquals("alias-x", test.getAlias());
        assertEquals("path", test.getRawPath());
        assertTrue(test.isRequired());
    }

    @Test
    public void shouldCompareProperly() {
        Descriptor base = TestDescriptor.create("a", "a", "a", true);
        Descriptor diffRequired = TestDescriptor.create("a", "a", "a", false);
        Descriptor diffLoader = TestDescriptor.create("b", "a", "a", true);
        Descriptor diffAlias = TestDescriptor.create("a", "b", "a", true);
        Descriptor diffPath = TestDescriptor.create("a", "a", "b", true);
        assertEquals(base, diffRequired);
        assertEquals(base.hashCode(), diffRequired.hashCode());
        assertEquals(base.toString(), diffRequired.toString());
        assertNotEquals(base, base.toString());

        assertNotEquals(base, diffLoader);
        assertNotEquals(base.hashCode(), diffLoader.hashCode());
        assertNotEquals(base.toString(), diffLoader.toString());
        assertTrue(base.compareTo(diffLoader) < 0);

        assertNotEquals(base, diffAlias);
        assertNotEquals(base.hashCode(), diffAlias.hashCode());
        assertNotEquals(base.toString(), diffAlias.toString());
        assertTrue(base.compareTo(diffAlias) < 0);

        assertNotEquals(base, diffPath);
        assertNotEquals(base.hashCode(), diffPath.hashCode());
        assertNotEquals(base.toString(), diffPath.toString());
        assertTrue(base.compareTo(diffPath) < 0);

        assertTrue(diffAlias.compareTo(diffLoader) > 0);
        assertTrue(diffAlias.compareTo(diffPath) > 0);
    }

}
