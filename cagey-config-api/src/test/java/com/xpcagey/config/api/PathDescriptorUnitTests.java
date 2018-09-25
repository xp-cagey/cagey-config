package com.xpcagey.config.api;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PathDescriptorUnitTests {
    @Test
    public void shouldConstructProperly() {
        PathDescriptor desc = new PathDescriptor("file", "/config.json", "disk", true);
        assertTrue(desc.isRequired());
        assertEquals("file", desc.getProvider());
        assertEquals("/config.json", desc.getRawPath());
        assertEquals(desc, desc.resolve(null));
        assertEquals(Collections.emptyList(), desc.getResolverInputs());
    }
}
