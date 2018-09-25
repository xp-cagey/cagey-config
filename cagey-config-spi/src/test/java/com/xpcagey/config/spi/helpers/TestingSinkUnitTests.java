package com.xpcagey.config.spi.helpers;

import com.xpcagey.config.spi.ValueCommand;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestingSinkUnitTests {
    @Test
    public void shouldReportSetValuesCorrectly() {
        TestingSink sink = new TestingSink();
        assertNull(sink.get("key", false));
        assertNull(sink.get("key", true));
        sink.set("key", false, "x");
        assertEquals("x", sink.get("key", false));
        assertNull(sink.get("key", true));
        sink.set("key", true, "x");
        assertEquals("x", sink.get("key", true));
        assertNull(sink.get("key", false));
        sink.clear("key"); // idempotent
        sink.clear("key"); // idempotent
        assertNull(sink.get("key", false));
        assertNull(sink.get("key", true));
    }

    @Test
    public void shouldReportSetValueTypesCorrectly() {
        TestingSink sink = new TestingSink();
        sink.set("key", false, 1L);
        assertEquals(1L, sink.get("key", false));
        sink.set("key", false, 1.0);
        assertEquals(1.0, sink.get("key", false));
        sink.set("key", false, true);
        assertEquals(true, sink.get("key", false));
        sink.set("key", false, true);
        assertEquals(true, sink.get("key", false));
        sink.set("key", false, Duration.ofSeconds(1));
        assertEquals(Duration.ofSeconds(1), sink.get("key", false));
        sink.set("key", false, Instant.ofEpochMilli(235));
        assertEquals(Instant.ofEpochMilli(235), sink.get("key", false));
        sink.set("key", false, "value");
        assertEquals("value", sink.get("key", false));
        sink.forceDefault("key");
        assertEquals(TestingSink.USE_DEFAULT, sink.get("key", false));
    }

    @Test
    public void shouldHandleIteratorsProperly() {
        List<ValueCommand> list = new ArrayList<>();
        list.add(ValueCommand.set("boolean", false, true));
        list.add(ValueCommand.set("string", false, "value"));
        TestingSink sink = new TestingSink();
        sink.addAll(list.iterator());
        assertEquals(true, sink.get("boolean", false));
        assertEquals("value", sink.get("string", false));
    }
}
