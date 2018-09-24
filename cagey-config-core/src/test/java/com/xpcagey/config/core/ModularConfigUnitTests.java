package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.BooleanElement;
import com.xpcagey.config.element.LongElement;
import com.xpcagey.config.element.RawValueElement;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ModularConfigUnitTests {
    private final Executor exec = Runnable::run; // inline exec

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConfigModule module;

    @Test
    public void shouldConstructProperly() {
        ModularConfig cfg = new ModularConfig(exec, "test");
        assertEquals("test", cfg.getName());
        assertEquals(0, cfg.getAll().size());
        assertFalse(cfg.hasKey("key"));
        assertNull(cfg.getOrNull("key"));
        assertFalse(cfg.iterator().hasNext());
        cfg.close();
    }

    @Test
    public void shouldAcceptModules() {
        when(module.iterator()).thenReturn(Collections.emptyIterator());

        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.append(module);
        cfg.close();
        verify(module).iterator();
        verify(module).addListener(any());
        verify(module).addReaper(any());
        verify(module).removeListener(any());
        verify(module).removeReaper(any());
        verifyNoMoreInteractions(module);
    }

    @Test
    public void shouldSpawnAndCleanupSubtrees() {
        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.subtree("prefix");
        cfg.close();
    }

    @Test
    public void shouldForwardModuleInitialization() {
        RawValueElement ele = new BooleanElement("key", true, true);
        Consumer<Element> listener = mock(ElementConsumer.class);
        Consumer<Element> trigger = mock(ElementConsumer.class);

        when(module.iterator()).thenReturn(Collections.singleton(ele).iterator());
        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.addListener(listener);
        cfg.addTrigger("key", trigger);
        cfg.append(module);
        assertNotNull(cfg.getOrNull("key"));
        cfg.get("key").ifPresent(e -> assertTrue(e.getAsBoolean()));
        assertEquals(1, cfg.getAll().size());
        cfg.close();
        cfg.removeListener(listener);
        cfg.removeTrigger("key", trigger);
        verify(listener).accept(any());
        verify(trigger).accept(any());
        verifyNoMoreInteractions(listener, trigger);
    }

    @Test
    public void shouldTriggerOnAddIfValueExists() {
        RawValueElement ele = new BooleanElement("key", true, true);
        when(module.iterator()).thenReturn(Collections.singleton(ele).iterator());
        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.append(module);
        assertNotNull(cfg.getOrNull("key"));

        Consumer<Element> trigger = mock(ElementConsumer.class);
        cfg.addTrigger("key", trigger);
        verify(trigger).accept(any());

        cfg.close();
    }

    @Test
    public void shouldIgnoreOverriddenValuesDuringInitialization() {
        RawValueElement ele = new BooleanElement("key", true, true);
        Consumer<Element> listener = mock(ElementConsumer.class);
        Consumer<Element> trigger = mock(ElementConsumer.class);

        ConfigModule module2 = mock(ConfigModule.class);
        RawValueElement ele2 = new LongElement("key", true, 123L);

        when(module.iterator()).thenReturn(Collections.singleton(ele).iterator());
        when(module2.iterator()).thenReturn(Collections.singleton(ele2).iterator());
        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.addListener(listener);
        cfg.addTrigger("key", trigger);
        cfg.append(module);
        cfg.append(module2);

        // no change to public view
        assertNotNull(cfg.getOrNull("key"));
        cfg.get("key").ifPresent(e -> assertTrue(e.getAsBoolean()));
        assertEquals(1, cfg.getAll().size());
        cfg.close();
        cfg.removeListener(listener);
        cfg.removeTrigger("key", trigger);
        verify(listener).accept(any());
        verify(trigger).accept(any());
        verifyNoMoreInteractions(listener, trigger);
    }

    @Test
    public void shouldHandleModuleEvents() {
        RawValueElement ele = new BooleanElement("key", true, true);
        RawValueElement ele2 = new BooleanElement("key2", true, true);
        Consumer<Element> listener = mock(ElementConsumer.class);
        Consumer<Element> trigger = mock(ElementConsumer.class);

        final AtomicReference<Consumer<Element>> configListener = new AtomicReference<>();
        final AtomicReference<Consumer<String>> configReaper = new AtomicReference<>();

        doAnswer(inv -> {
            configListener.set(inv.getArgument(0));
            return null;
        }).when(module).addListener(any());
        doAnswer(inv -> {
            configReaper.set(inv.getArgument(0));
            return null;
        }).when(module).addReaper(any());

        when(module.iterator()).thenReturn(Collections.emptyIterator());
        ModularConfig cfg = new ModularConfig(exec, "test");
        cfg.addListener(listener);
        cfg.addTrigger("key", trigger);
        cfg.append(module);

        configListener.get().accept(ele); // new element
        configListener.get().accept(ele); // repeat, should not forward
        configListener.get().accept(ele2); // new element
        configReaper.get().accept(ele.getKey()); // drop element
        configReaper.get().accept(ele.getKey()); // repeat drop
        cfg.close();

        verify(listener, times(2)).accept(any());
        verify(trigger).accept(any());
        verifyNoMoreInteractions(listener, trigger);
    }
}
