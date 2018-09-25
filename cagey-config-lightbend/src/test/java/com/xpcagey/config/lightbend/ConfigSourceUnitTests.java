package com.xpcagey.config.lightbend;

import com.xpcagey.config.spi.helpers.TestingSink;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * These values come from reference.conf
 */
public class ConfigSourceUnitTests {
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void shouldLoadValuesCorrectly() {
        try (ConfigSource src = new ConfigSource(classLoader, "/")) {
            assertEquals(src.getPath(), "/");
            TestingSink sink = new TestingSink();
            src.initialize(sink);
            assertEquals(true, sink.get("values.boolean", false));
            assertEquals(3L, sink.get("values.long", false));
            assertEquals(3.1, sink.get("values.double", false));
            assertEquals("value", sink.get("values.string", false));
            assertEquals(TestingSink.USE_DEFAULT, sink.get("values.empty", false));
            assertEquals("a", sink.get("values.list[0]", false));
            assertEquals("b", sink.get("values.list[1]", false));
            assertEquals(true, sink.get("values.subobject[0].boolean", false));
        }
    }

    @Test
    public void shouldIgnoreRegister() {
        try (ConfigSource src = new ConfigSource(classLoader,"/")) {
            src.register(null);
            src.unregister(null);
        }
    }

    @Test
    public void shouldHandlePath() {
        try (ConfigSource src = new ConfigSource(classLoader,"custom.conf")) {
            assertEquals(src.getPath(), "custom.conf");
            TestingSink sink = new TestingSink();
            src.initialize(sink);
            assertEquals(false, sink.get("values.boolean", false));
            assertEquals(2L, sink.get("values.long", false));
        }
    }

    @Test
    public void shouldSupportIterator() {
        try (ConfigSource src = new ConfigSource(classLoader,"custom.conf")) {
            TestingSink sink = new TestingSink();
            sink.addAll(src.iterator());
            assertEquals(false, sink.get("values.boolean", false));
            assertEquals(2L, sink.get("values.long", false));
        }
    }
}
