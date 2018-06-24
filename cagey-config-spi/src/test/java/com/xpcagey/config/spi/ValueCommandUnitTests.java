package com.xpcagey.config.spi;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.Mockito.*;

public class ValueCommandUnitTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConfigSink sink;

    @Test
    public void shouldClearProperly() {
        ValueCommand.clear("key-x").apply(sink);
        verify(sink).clear("key-x");
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetBooleanProperly() {
        ValueCommand.set("key-x", false, false).apply(sink);
        verify(sink).set("key-x", false, false);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetDoubleProperly() {
        ValueCommand.set("key-x", false, 1.0).apply(sink);
        verify(sink).set("key-x", false, 1.0);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetDurationProperly() {
        ValueCommand.set("key-x", false, Duration.ofSeconds(3)).apply(sink);
        verify(sink).set("key-x", false, Duration.ofSeconds(3));
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetInstantProperly() {
        Instant now = Instant.now();
        ValueCommand.set("key-x", false, now).apply(sink);
        verify(sink).set("key-x", false, now);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetLongProperly() {
        ValueCommand.set("key-x", false, 7L).apply(sink);
        verify(sink).set("key-x", false, 7L);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetStringProperly() {
        ValueCommand.set("key-x", false, "value").apply(sink);
        verify(sink).set("key-x", false, "value");
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveBooleanProperly() {
        ValueCommand.set("key-x", true, false).apply(sink);
        verify(sink).set("key-x", true, false);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveDoubleProperly() {
        ValueCommand.set("key-x", true, 1.0).apply(sink);
        verify(sink).set("key-x", true, 1.0);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveDurationProperly() {
        ValueCommand.set("key-x", true, Duration.ofSeconds(3)).apply(sink);
        verify(sink).set("key-x", true, Duration.ofSeconds(3));
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveInstantProperly() {
        Instant now = Instant.now();
        ValueCommand.set("key-x", true, now).apply(sink);
        verify(sink).set("key-x", true, now);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveLongProperly() {
        ValueCommand.set("key-x", true, 7L).apply(sink);
        verify(sink).set("key-x", true, 7L);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldSetSensitiveStringProperly() {
        ValueCommand.set("key-x", true, "value").apply(sink);
        verify(sink).set("key-x", true, "value");
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldForceDefaultProperly() {
        ValueCommand.forceDefault("key-x").apply(sink);
        verify(sink).forceDefault("key-x");
        verifyNoMoreInteractions(sink);
    }


}
