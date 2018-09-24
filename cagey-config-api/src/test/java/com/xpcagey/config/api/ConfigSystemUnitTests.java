package com.xpcagey.config.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ConfigSystemUnitTests {
    final Executor exec = Runnable::run; // inline execution
    final ClassLoader classLoader = getClass().getClassLoader();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Iterable<ConfigEngine> mockLoader;

    @Before
    public void setup() {
        ConfigSystem.reset(mockLoader);
    }

    @Test
    public void shouldThrowWhenNoLoadersExist() {
        when(mockLoader.iterator()).thenReturn(Collections.emptyIterator());

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No ConfigEngine has been found on the ClassPath");

        ConfigSystem.setDefault("string-x", "value");
    }

    @Test
    public void shouldThrowWhenMultipleLoadersExist() {
        ConfigEngine engine = mock(ConfigEngine.class);
        ConfigEngine engine2 = mock(ConfigEngine.class);
        when(mockLoader.iterator()).thenReturn(Arrays.asList(engine, engine2).iterator());

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Multiple ConfigEngines have been found on the ClassPath");

        ConfigSystem.setDefault("string-x", "value");
    }

    @Test
    public void shouldInitializeExactlyOnce() throws InterruptedException {
        ConfigEngine engine = mock(ConfigEngine.class);
        CountDownLatch stalled = new CountDownLatch(1);
        CountDownLatch entered = new CountDownLatch(1);

        when(mockLoader.iterator()).thenAnswer(inv -> {
            entered.countDown();
            stalled.await();
            return Collections.singleton(engine).iterator();
        });

        Thread t1 = new Thread(() -> ConfigSystem.setDefault("string-x", "value"));
        Thread t2 = new Thread(() -> ConfigSystem.setDefault("bool-x", false));
        Thread t3 = new Thread(() -> ConfigSystem.setDefault("int-x", 2));

        t1.start();
        t2.start();
        t3.start();

        // don't take action until all threads are in a block/wait/timed wait
        entered.await();
        while (
            t1.getState() == Thread.State.RUNNABLE ||
            t2.getState() == Thread.State.RUNNABLE ||
            t3.getState() == Thread.State.RUNNABLE
        ) { Thread.yield(); }

        // then allow them to continue
        stalled.countDown();

        t1.join();
        t2.join();
        t3.join();

        verify(mockLoader).iterator();
        verifyNoMoreInteractions(mockLoader);
    }

    @Test
    public void shouldForwardCallsToEngine() throws ConfigLoadException {
        Descriptor desc = new PathDescriptor("loader", "path", "alias", true);
        Config config = mock(Config.class);

        ConfigEngine engine = mock(ConfigEngine.class);
        doReturn(config).when(engine).load(any(), any(), any(), any());

        when(mockLoader.iterator()).thenReturn(Collections.singleton(engine).iterator());

        ConfigSystem.setDefault("string-x", "value");
        ConfigSystem.setDefault("int-x", 1);
        ConfigSystem.setDefault("float-x", 1.0f);
        ConfigSystem.setDefault("instant-x", Instant.ofEpochMilli(123));
        ConfigSystem.setDefault("duration-x", Duration.ofSeconds(3));
        ConfigSystem.setDefault("bool-x", false);
        assertEquals(config, ConfigSystem.load("config", classLoader, exec, desc));

        verify(engine).setDefault("string-x", "value");
        verify(engine).setDefault("int-x", 1);
        verify(engine).setDefault("float-x", 1.0f);
        verify(engine).setDefault("instant-x", Instant.ofEpochMilli(123));
        verify(engine).setDefault("duration-x", Duration.ofSeconds(3));
        verify(engine).setDefault("bool-x", false);
        verify(engine).load("config", classLoader, exec, desc);
        verifyNoMoreInteractions(engine);
    }

    @Test
    public void shouldAllowLegacyFormatCall() throws ConfigLoadException {
        Descriptor desc = new PathDescriptor("loader", "path", "alias", true);
        Config config = mock(Config.class);

        ConfigEngine engine = mock(ConfigEngine.class);
        doReturn(config).when(engine).load(any(), any(), any(), any());

        when(mockLoader.iterator()).thenReturn(Collections.singleton(engine).iterator());
        assertEquals(config, ConfigSystem.load("config", exec, desc));

        verify(engine).load("config", ConfigSystem.class.getClassLoader(), exec, desc);
        verifyNoMoreInteractions(engine);
    }
}
