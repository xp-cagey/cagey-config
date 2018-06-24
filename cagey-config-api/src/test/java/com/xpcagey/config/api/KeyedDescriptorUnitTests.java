package com.xpcagey.config.api;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class KeyedDescriptorUnitTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldConstructProperly() {
        Config config = mock(Config.class);
        Descriptor desc = new KeyedDescriptor("provider-x", "key-x", "alias-x", true, "source-1", "source-2", "source-3");
        assertTrue(desc.isRequired());
        assertEquals("provider-x", desc.getProvider());
        assertEquals("alias-x", desc.getAlias());
        assertEquals( "key-x@source-1::source-2::source-3", desc.getRawPath());
        assertEquals(Arrays.asList("source-1", "source-2", "source-3"), desc.getResolverInputs());
    }

    @Test
    public void shouldForwardMissingElementExceptions() throws MissingElementException, ConfigLoadException {
        Config config = mock(Config.class);
        doThrow(MissingElementException.class).when(config).getOrThrow("key-x");

        thrown.expect(MissingResolverFieldException.class);

        Descriptor desc = new KeyedDescriptor("provider-x", "key-x", "alias-x", true, "source-1", "source-2", "source-3");
        desc.resolve(config);
    }

    @Test
    public void shouldResolvePaths() throws MissingElementException, ConfigLoadException {
        final String path = "/value";

        Element element = mock(Element.class);
        when(element.getAsString()).thenReturn(path);

        Config config = mock(Config.class);
        doReturn(element).when(config).getOrThrow(anyString());

        Descriptor desc = new KeyedDescriptor("provider-x", "key-x", "alias-x", true, "source-1", "source-2", "source-3");
        Descriptor resolved = desc.resolve(config);
        assertEquals(resolved.isRequired(), desc.isRequired());
        assertEquals(resolved.getProvider(), desc.getProvider());
        assertEquals(path, resolved.getRawPath());
        assertEquals(Collections.emptyList(), resolved.getResolverInputs());

        verify(config).getOrThrow("key-x");
        verify(element).getAsString();
        verifyNoMoreInteractions(config, element);
    }
}
