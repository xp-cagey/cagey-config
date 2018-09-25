package com.xpcagey.config.spi;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ValueCommandIterableUnitTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConfigSink sink;

    @Mock
    private ConfigSource source;

    @Test
    public void shouldForwardCalls() {
        doAnswer(inv -> {
            ConfigSink sink = inv.getArgument(0);
            sink.set("boolean", false, false);
            sink.set("double", false, 1.0);
            sink.set("duration", false, Duration.ZERO);
            sink.set("instant", false, Instant.EPOCH);
            sink.set("long", false, 0L);
            sink.set("string", false, "");
            sink.clear("none");
            sink.forceDefault("default");
            return null;
        }).when(source).initialize(any());
        ValueCommandIterable it = new ValueCommandIterable(source);
        for(ValueCommand cmd : it) {
            cmd.apply(sink);
        }
        verify(sink).set("boolean", false, false);
        verify(sink).set("double", false, 1.0);
        verify(sink).set("duration", false, Duration.ZERO);
        verify(sink).set("instant", false, Instant.EPOCH);
        verify(sink).set("long", false, 0L);
        verify(sink).set("string", false, "");
        verify(sink).clear("none");
        verify(sink).forceDefault("default");
        verifyNoMoreInteractions(sink);
    }
}
