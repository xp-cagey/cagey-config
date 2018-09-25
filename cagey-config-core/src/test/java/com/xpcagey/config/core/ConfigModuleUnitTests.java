package com.xpcagey.config.core;

import com.xpcagey.config.api.Descriptor;
import com.xpcagey.config.element.RawValueElement;
import com.xpcagey.config.spi.ConfigSource;
import com.xpcagey.config.spi.ValueCommand;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConfigModuleUnitTests {
    private final Executor exec = Runnable::run; // inline execution

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock private Descriptor desc;
    @Mock private ConfigSource src;

    @Test
    public void checkConstruction() {
        when(desc.getProvider()).thenReturn("provider");
        when(desc.getAlias()).thenReturn("test");
        when(desc.getRawPath()).thenReturn("raw-path");
        when(src.getPath()).thenReturn("path");

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertEquals("test", module.getAlias());
        assertEquals("raw-path", module.getRawPath());
        assertEquals("path", module.getPath());
        assertEquals("provider", module.getProvider());
        assertNull(module.getOrNull("key"));
        assertFalse(module.iterator().hasNext());
        module.close();

        assertNull(verify(desc).getAlias());
        assertNull(verify(desc).getProvider());
        verify(desc).getRawPath();
        verify(src).getPath();
        verify(src).register(any());
    }

    @Test
    public void checkValueReturn() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("key", false, "value"));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("key"));
        assertTrue(module.iterator().hasNext());
        module.close();
    }

    @Test
    public void checkValueClear() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("key", false, "value"));
            sink.accept(ValueCommand.clear("key"));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNull(module.getOrNull("key"));
        assertFalse(module.iterator().hasNext());
        module.close();
    }

    @Test
    public void checkForceDefault() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("key", false, "value"));
            sink.accept(ValueCommand.forceDefault("key"));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("key"));
        assertTrue(module.iterator().hasNext());
        module.close();
    }

    @Test
    public void checkListeners() {
        final Set<Consumer<ValueCommand>> sinks = new HashSet<>();
        doAnswer(inv -> {
            sinks.add(inv.getArgument(0));
            return null;
        }).when(src).register(any());

        Consumer<RawValueElement> listener = mock(RawValueElementConsumer.class);
        Consumer<RawValueElement> trigger = mock(RawValueElementConsumer.class);
        Consumer<String> reaper = mock(StringConsumer.class);

        Executor exec = Runnable::run; // inline execution
        ConfigModule module = new ConfigModule(exec, desc, src);
        module.addListener(listener);
        module.addListener(listener); // idempotent
        module.addTrigger("key", trigger);
        module.addTrigger("key", trigger); // idempotent
        module.addReaper(reaper);
        module.addReaper(reaper); // idempotent

        Consumer<ValueCommand> sink = sinks.iterator().next();
        sink.accept(ValueCommand.set("key", false, "value"));
        sink.accept(ValueCommand.set("key", false, "value2"));
        sink.accept(ValueCommand.set("key2", false, "value"));
        sink.accept(ValueCommand.clear("key"));
        sink.accept(ValueCommand.clear("key")); /// should have no side effects
        module.removeListener(listener);
        module.removeTrigger("key", trigger);
        module.removeReaper(reaper);
        sink.accept(ValueCommand.set("key", false, "value")); // should not trigger or call listener
        sink.accept(ValueCommand.clear("key")); // should not call reaper
        module.removeListener(listener); // idempotent
        module.removeTrigger("key2", trigger); // should have no harmful side effects
        module.removeReaper(reaper); // idempotent

        verify(listener, times(3)).accept(any());
        verifyNoMoreInteractions(listener);
        verify(trigger, times(2)).accept(any());
        verifyNoMoreInteractions(trigger);
        verify(reaper, times(2)).accept(any());
        verifyNoMoreInteractions(reaper);
    }

    @Test
    public void checkSetBoolean() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("boolean-key", false, true));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("boolean-key"));
        assertTrue(module.getOrNull("boolean-key").getAsBoolean());
    }

    @Test
    public void checkSetDouble() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("double-key", false, 123.456));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("double-key"));
        assertEquals(123.456, module.getOrNull("double-key").getAsDouble(), 0.000001);
    }

    @Test
    public void checkSetDuration() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("duration-key", false, Duration.ofMinutes(123)));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("duration-key"));
        assertEquals(Duration.ofMinutes(123), module.getOrNull("duration-key").getAsDuration());
    }

    @Test
    public void checkSetInstant() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("instant-key", false, Instant.ofEpochSecond(1234567123456789L)));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("instant-key"));
        assertEquals(Instant.ofEpochSecond(1234567123456789L), module.getOrNull("instant-key").getAsInstant());
    }

    @Test
    public void checkSetLong() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("long-key", false, -123456789123456789L));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("long-key"));
        assertEquals(-123456789123456789L, module.getOrNull("long-key").getAsLong());
    }

    @Test
    public void checkSetString() {
        doAnswer(inv -> {
            Consumer<ValueCommand> sink = inv.getArgument(0);
            sink.accept(ValueCommand.set("string-key", false, "This is the value!"));
            return null;
        }).when(src).register(any());

        ConfigModule module = new ConfigModule(exec, desc, src);
        assertNotNull(module.getOrNull("string-key"));
        assertEquals("This is the value!", module.getOrNull("string-key").getAsString());
    }
}
