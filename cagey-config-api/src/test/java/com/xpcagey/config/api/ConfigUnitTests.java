package com.xpcagey.config.api;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConfigUnitTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldForwardMissingCorrectly() throws MissingElementException {
        Config cfg = mock(Config.class);
        when(cfg.getOrNull(any())).thenReturn(null);
        when(cfg.get(any())).thenCallRealMethod();
        when(cfg.hasKey(any())).thenCallRealMethod();
        doCallRealMethod().when(cfg).getOrThrow(any());

        assertFalse(cfg.hasKey("key"));
        assertEquals(Optional.empty(), cfg.get("key"));

        thrown.expect(MissingElementException.class);
        cfg.getOrThrow("key");
    }

    @Test
    public void shouldForwardPresentCorrectly() throws MissingElementException {
        Element e = mock(Element.class);

        Config cfg = mock(Config.class);
        when(cfg.getOrNull(any())).thenReturn(e);
        when(cfg.get(any())).thenCallRealMethod();
        when(cfg.hasKey(any())).thenCallRealMethod();
        doCallRealMethod().when(cfg).getOrThrow(any());

        assertTrue(cfg.hasKey("key"));
        assertEquals(Optional.of(e), cfg.get("key"));
        assertEquals(e, cfg.getOrThrow("key"));
    }
}
