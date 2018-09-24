package com.xpcagey.config.core;

import com.xpcagey.config.api.*;
import com.xpcagey.config.element.DefaultManagement;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.Executor;

import static org.junit.Assert.*;

public class ConfigEngineUnitTests {
    private final Executor exec = Runnable::run; // inline execution
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldForwardDefaultsCorrectly() throws ConfigLoadException, MissingElementException {
        ConfigEngine engine = new ConfigEngine();
        engine.setDefault("boolean-x", true);
        engine.setDefault("double-x", 123.456);
        engine.setDefault("duration-x", Duration.ofMinutes(12));
        engine.setDefault("instant-x", Instant.ofEpochMilli(999));
        engine.setDefault("string-x", "value");
        engine.setDefault("long-x", 1L);

        Config defaults = engine.load("defaults", classLoader, exec);
        assertTrue(defaults.getOrThrow("boolean-x").getAsBoolean());
        assertEquals(123.456, defaults.getOrThrow("double-x").getAsDouble(), 0.00001);
        assertEquals(Duration.ofMinutes(12), defaults.getOrThrow("duration-x").getAsDuration());
        assertEquals(Instant.ofEpochMilli(999), defaults.getOrThrow("instant-x").getAsInstant());
        assertEquals(1L, defaults.getOrThrow("long-x").getAsLong());
        assertEquals("value", defaults.getOrThrow("string-x").getAsString());

        thrown.expect(MissingElementException.class);
        thrown.expectMessage("Config [defaults] does not contain mandatory key [unset-key]");

        defaults.getOrThrow("unset-key");
        assertFalse(defaults.getSources().hasNext());
    }

    @Test
    public void shouldResolveMandatoryElements() throws ConfigLoadException {
        Descriptor desc = new PathDescriptor("mock", "path", "mock", true);
        ConfigEngine engine = new ConfigEngine();
        Config config = engine.load("mock", classLoader, exec, desc);
        engine.load("mock", classLoader, exec, desc); // should only load providers once
        Iterator<String> sources = config.getSources();
        assertTrue(sources.hasNext());
        assertEquals("mock:path", sources.next());
        assertFalse(sources.hasNext());
    }

    @Test
    public void shouldThrowIfMissingMandatoryElements() throws ConfigLoadException {
        thrown.expect(MissingLoaderException.class);
        ConfigEngine engine = new ConfigEngine();
        engine.load("mock", classLoader, exec, new PathDescriptor("mocked", "/", "mocked", true));
    }

    @Test
    public void shouldReportMissingRequirements() throws ConfigLoadException {
        thrown.expect(MissingConfigException.class);
        ConfigEngine engine = new ConfigEngine();
        engine.load("mock", classLoader, exec, new KeyedDescriptor("mock", ".", "mock", true, "missing"));
    }

    @Test
    public void shouldForwardIllegalPathExceptions() throws ConfigLoadException {
        thrown.expect(IllegalPathException.class);
        ConfigEngine engine = new ConfigEngine();
        engine.load("mock", classLoader, exec, new PathDescriptor("illegal", "/", "illegal", true));
    }

    @Test
    public void shouldResolveKeyedDescriptors() throws ConfigLoadException {
        ConfigEngine engine = new ConfigEngine();
        engine.setDefault("mock1.path", "/1");
        engine.load("mock", classLoader, exec,
                new KeyedDescriptor("mock", "mock1.path", "mock1", true, "mock2"),
                new PathDescriptor("mock", "2", "mock2", true)
        );
    }

    @Test
    public void shouldForwardMissingResolverFieldExceptions() throws ConfigLoadException {
        thrown.expect(MissingResolverFieldException.class);
        ConfigEngine engine = new ConfigEngine();
        DefaultManagement.reset();
        engine.load("mock", classLoader, exec,
                new KeyedDescriptor("mock", "mock1.path", "mock1", true, "mock2"),
                new PathDescriptor("mock", "2", "mock2", true)
        );
    }

    @Test
    public void shouldReportResolverDeadlock() throws ConfigLoadException {
        thrown.expect(CircularRequirementsException.class);
        ConfigEngine engine = new ConfigEngine();
        engine.load("mock", classLoader, exec,
                new KeyedDescriptor("mock", "mock1.path", "mock1", true, "mock2"),
                new KeyedDescriptor("mock", "mock2.path", "mock2", true, "mock1")
        );
    }

    @Test
    public void shouldSkipOptionalRequirementsWhenResolving() throws ConfigLoadException {
        ConfigEngine engine = new ConfigEngine();
        engine.setDefault("mock1.path", "/1");
        engine.load("mock", classLoader, exec,
                new PathDescriptor("missing", "/", "missing", false),
                new PathDescriptor("mock", "2", "mock", true),
                new KeyedDescriptor("mock", "mock1.path", "mock1", true, "missing")
        );
    }
}
