package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class ConfigEventBindingsUnitTests {
    private final Executor exec = Runnable::run; // inline execution

    @Test
    public void shouldNotifyOnRemoval() {
        Consumer<String> onRemove = mock(StringConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addReaper(onRemove);
        bindings.notifyRemoved("key");
        bindings.removeReaper(onRemove);
        bindings.notifyRemoved("key2");

        verify(onRemove).accept("key");
        verifyNoMoreInteractions(onRemove);
    }

    @Test
    public void shouldCleanReferencesOnRemoval() {
        Consumer<String> onRemove = mock(StringConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addReaper(k -> { onRemove.accept(k); onRemove.accept(k); }); // weak reference only
        System.gc();
        bindings.notifyRemoved("key");
        verifyNoMoreInteractions(onRemove); // should not receive forwarded calls from destructed wrapper
    }

    @Test
    public void shouldNotifyOnTrigger() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");
        Element e2 = mock(Element.class);
        when(e2.getKey()).thenReturn("key2");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addTrigger("key", onTrigger);
        bindings.notify(e);
        bindings.notify(e2);

        verify(e).getKey();
        verifyNoMoreInteractions(e);

        verify(onTrigger).accept(e);
        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldNotifyOnTriggerForListeners() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");
        Element e2 = mock(Element.class);
        when(e2.getKey()).thenReturn("key2");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addListener(onTrigger);
        bindings.notify(e);
        bindings.notify(e2);

        verify(e).getKey();
        verifyNoMoreInteractions(e);
        verify(e2).getKey();
        verifyNoMoreInteractions(e2);

        verify(onTrigger).accept(e);
        verify(onTrigger).accept(e2);
        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldRemoveTriggersProperly() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addListener(onTrigger);
        bindings.removeListener(onTrigger);
        bindings.notify(e);

        verify(e).getKey();
        verifyNoMoreInteractions(e);

        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldCleanReferencesOnNotify() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addTrigger("key", k -> { onTrigger.accept(k); onTrigger.accept(k); });
        System.gc();
        bindings.notify(e);

        verify(e, times(2)).getKey(); // check and removal
        verifyNoMoreInteractions(e);
        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldCheckForCleaningOnAddTrigger() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addTrigger("key", k -> { onTrigger.accept(k); onTrigger.accept(k); });
        bindings.clear(); // simulate reference cleaned by background thread while we're waiting for lock
        bindings.addTrigger("key", onTrigger);
        bindings.notify(e);

        verify(e).getKey();
        verifyNoMoreInteractions(e);

        verify(onTrigger).accept(e); // once from explicit binding, weak binding does not fire
        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldCleanReferencesOnRemoveTrigger() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addTrigger("key", onTrigger);
        bindings.addTrigger("key", k -> { onTrigger.accept(k); onTrigger.accept(k); });
        System.gc();
        bindings.removeTrigger("key", onTrigger);
        bindings.notify(e);
        bindings.removeTrigger("key", onTrigger); // idempotent

        verify(e).getKey(); // check, removal not necessary
        verifyNoMoreInteractions(e);
        verifyNoMoreInteractions(onTrigger);
    }

    @Test
    public void shouldAllowPartialRemovalOnRemoveTrigger() {
        Element e = mock(Element.class);
        when(e.getKey()).thenReturn("key");

        Consumer<Element> onTrigger = mock(ElementConsumer.class);
        Consumer<Element> onTrigger2 = mock(ElementConsumer.class);

        ConfigEventBindings<Element> bindings = new ConfigEventBindings<>(exec);
        bindings.addTrigger("key", onTrigger);
        bindings.addTrigger("key", onTrigger2);
        System.gc();
        bindings.removeTrigger("key", onTrigger2);
        bindings.notify(e);

        verify(e).getKey(); // check, removal not necessary
        verifyNoMoreInteractions(e);
        verify(onTrigger).accept(e);
        verifyNoMoreInteractions(onTrigger);
        verifyNoMoreInteractions(onTrigger2);
    }
}
