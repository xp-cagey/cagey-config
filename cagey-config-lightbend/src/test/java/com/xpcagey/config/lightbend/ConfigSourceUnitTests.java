package com.xpcagey.config.lightbend;

import com.xpcagey.config.spi.ValueCommand;
import com.xpcagey.config.spi.helpers.TestingSink;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * These values come from reference.conf
 */
public class ConfigSourceUnitTests {
    @Test
    public void shouldLoadValuesCorrectly() {
        try (ConfigSource src = new ConfigSource("/")) {
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
        try (ConfigSource src = new ConfigSource("/")) {
            src.register(null);
            src.unregister(null);
        }
    }

    @Test
    public void shouldHandlePath() {
        try (ConfigSource src = new ConfigSource("custom.conf")) {
            assertEquals(src.getPath(), "custom.conf");
            TestingSink sink = new TestingSink();
            src.initialize(sink);
            assertEquals(false, sink.get("values.boolean", false));
            assertEquals(2L, sink.get("values.long", false));
        }
    }

    @Test
    public void shouldSupportIterator() {
        try (ConfigSource src = new ConfigSource("custom.conf")) {
            TestingSink sink = new TestingSink();
            sink.addAll(src.iterator());
            assertEquals(false, sink.get("values.boolean", false));
            assertEquals(2L, sink.get("values.long", false));
        }
    }
}
