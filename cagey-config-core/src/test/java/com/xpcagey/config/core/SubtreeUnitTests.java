package com.xpcagey.config.core;

import com.xpcagey.config.api.Config;
import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.BooleanElement;
import com.xpcagey.config.element.RawValueElement;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SubtreeUnitTests {
    private final Executor exec = Runnable::run; // inline execution

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void shouldRegisterAndUnregisterForUpdates() {
        ModularConfig cfg = mock(ModularConfig.class);
        Subtree sub = new Subtree(cfg, exec,"prefix");
        verify(cfg).attach(any(), any());
        sub.close();
        verify(cfg).detach(any(), any());
        verifyNoMoreInteractions(cfg);
    }

    @Test
    public void shouldForwardRequestsWithPrefix() {
        ModularConfig cfg = mock(ModularConfig.class);
        when(cfg.getOrNull(anyString())).thenReturn(null);
        when(cfg.entrySet()).thenReturn(new HashSet<>());
        Subtree sub = new Subtree(cfg, exec,"prefix");
        assertNull(sub.getOrNull("key"));
        assertTrue(sub.getAll().isEmpty());
        sub.close();
        verify(cfg).getOrNull("prefix.key");
        verify(cfg).entrySet();
    }

    @Test
    public void shouldTriggerOnAddIfValueExists() {
        RawValueElement ele = new BooleanElement("prefix.key", true, true);
        ModularConfig cfg = mock(ModularConfig.class);
        when(cfg.getOrNull(anyString())).thenReturn(ele);
        Subtree sub = new Subtree(cfg, exec,"prefix");
        assertNotNull(sub.getOrNull("key"));

        Consumer<Element> trigger = mock(ElementConsumer.class);
        sub.addTrigger("key", trigger);
        verify(trigger).accept(any());
        cfg.close();
    }

    @Test
    public void shouldHandleModuleEvents() {
        RawValueElement ele = new BooleanElement("prefix.key", true, true);
        RawValueElement ele2 = new BooleanElement("prefix.key2", true, true);
        RawValueElement ele3 = new BooleanElement("other.key3", true, true);
        Consumer<Element> listener = mock(ElementConsumer.class);
        Consumer<Element> trigger = mock(ElementConsumer.class);
        ModularConfig cfg = mock(ModularConfig.class);

        final AtomicReference<Consumer<Element>> configListener = new AtomicReference<>();
        final AtomicReference<Consumer<Boolean>> configDependency = new AtomicReference<>();

        doAnswer(inv -> {
            configListener.set(inv.getArgument(0));
            configDependency.set(inv.getArgument(1));
            return null;
        }).when(cfg).attach(any(), any());

        Subtree sub = new Subtree(cfg, exec, "prefix");
        sub.addListener(listener);
        sub.addTrigger("key", trigger);

        configDependency.get().accept(false); // should have no effect

        configListener.get().accept(ele); // new element
        configListener.get().accept(ele2); // new element
        configListener.get().accept(ele3); // new element, not a match

        sub.removeListener(listener);
        sub.removeTrigger("key", trigger);
        cfg.close();

        configListener.get().accept(ele); // new element
        configListener.get().accept(ele2); // new element
        configListener.get().accept(ele3); // new element, not a match

        verify(listener, times(2)).accept(any());
        verify(trigger).accept(any());
        verifyNoMoreInteractions(listener, trigger);
    }

    @Test
    public void shouldFilterIteration() {
        final ConcurrentHashMap<String, ImmutableSortedElementSet> values = new ConcurrentHashMap<>();
        values.put("prefix.key", new ImmutableSortedElementSet(0, new BooleanElement("prefix.key", true, true)));
        values.put("prefix.key2", new ImmutableSortedElementSet(0, new BooleanElement("prefix.key2", true, true)));
        values.put("other.key3", new ImmutableSortedElementSet(0, new BooleanElement("other.key3", true, true)));

        ModularConfig cfg = mock(ModularConfig.class);
        when(cfg.entrySet()).thenReturn(values.entrySet());

        Subtree sub = new Subtree(cfg, exec, "prefix");
        SortedMap<String, Element> out = sub.getAll();
        assertEquals(2, out.size());
        assertEquals("key", out.firstKey());
        assertEquals("key2", out.lastKey());

        Iterator<Element> e = sub.iterator();
        assertTrue(e.hasNext());
        assertEquals("key", e.next().getKey());
        assertTrue(e.hasNext());
        assertEquals("key2", e.next().getKey());
        assertFalse(e.hasNext());
    }

    @Test
    public void shouldAllowNesting() {
        Element expected = new BooleanElement("prefix.secondary.key", true, true);
        ModularConfig cfg = mock(ModularConfig.class);
        when(cfg.getSources()).thenReturn(Collections.singleton("source").iterator());
        when(cfg.getName()).thenReturn("test");
        doAnswer(inv -> new BooleanElement(inv.getArgument(0), true, true)).when(cfg).getOrNull(any());
        doAnswer(inv -> new Subtree(cfg, exec, inv.getArgument(0))).when(cfg).subtree(any());

        Subtree sub = new Subtree(cfg, exec, "prefix");
        Config inner = sub.subtree("secondary.");
        assertEquals("prefix.secondary.@test", inner.getName());

        Iterator<String> sources = inner.getSources();
        assertTrue(sources.hasNext());
        assertEquals("source", sources.next());
        assertFalse(sources.hasNext());

        Element e = inner.getOrNull("key");
        assertEquals("key", e.getKey());
        assertEquals(0, expected.compareTo(e));
        assertTrue(expected.hasEqualValue(e));
    }
}

