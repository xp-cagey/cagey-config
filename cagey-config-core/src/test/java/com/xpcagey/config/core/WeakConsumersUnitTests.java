package com.xpcagey.config.core;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WeakConsumersUnitTests {
    @Test
    public void checkLifecycleWithShouldExpireAndExplicitRemoval() {
        Consumer<String> consumer = mock(StringConsumer.class);
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(true);
        assertTrue(consumers.add(consumer));
        assertTrue(consumers.test(exec, "test"));
        assertTrue(consumers.test(exec, "test"));
        assertTrue(consumers.remove(consumer)); // indicates we have expired since collection is now empty
        assertTrue(consumers.remove(consumer)); // still report we're expired on reentry

        assertFalse(consumers.add(consumer)); // can't add after expiration
        assertFalse(consumers.test(exec, "test")); // doesn't trigger

        verify(consumer, times(2)).accept(any());
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void checkLifecycleWithShouldExpireAndExplicitPartialRemoval() {
        Consumer<String> consumer = mock(StringConsumer.class);
        Consumer<String> consumer2 = mock(StringConsumer.class);
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(true);
        assertTrue(consumers.add(consumer));
        assertTrue(consumers.add(consumer2));
        assertTrue(consumers.test(exec, "test"));
        assertTrue(consumers.test(exec, "test"));
        assertFalse(consumers.remove(consumer2)); // indicates we have not expired since collection is not empty

        assertTrue(consumers.add(consumer2)); // can still add after expiration
        assertTrue(consumers.test(exec, "test")); // triggers

        verify(consumer, times(3)).accept(any());
        verifyNoMoreInteractions(consumer);
        verify(consumer2, times(3)).accept(any());
        verifyNoMoreInteractions(consumer2);
    }

    @Test
    public void checkLifecycleWithoutShouldExpireAndExplicitRemoval() {
        Consumer<String> consumer = mock(StringConsumer.class);
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(false);
        assertTrue(consumers.add(consumer));
        assertTrue(consumers.test(exec, "test"));
        assertTrue(consumers.test(exec, "test"));
        assertFalse(consumers.remove(consumer)); // indicates we should not consider this collection expired

        assertTrue(consumers.add(consumer)); // add still ok after hitting size 0
        assertTrue(consumers.test(exec, "test")); // still triggers

        verify(consumer, times(3)).accept(any());
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void checkLifecycleWithShouldExpireAndImplicitRemoval() {
        final AtomicInteger count = new AtomicInteger();
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(true);
        assertTrue(consumers.add((s) -> count.incrementAndGet())); // will immediately be eligible for GC
        System.gc();

        assertFalse(consumers.test(exec, "test"));
        assertEquals(0, count.get());
    }

    @Test
    public void checkLifecycleWithoutShouldExpireAndImplicitRemoval() {
        final AtomicInteger count = new AtomicInteger();
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(false);
        assertTrue(consumers.add((s) -> count.incrementAndGet())); // will immediately be eligible for GC
        System.gc();

        assertTrue(consumers.test(exec, "test")); // still ok to execute
        assertEquals(0, count.get()); // implicit removal still took place
    }

    @Test
    public void checkLifecycleWithShouldExpireAndPartialRemoval() {
        final AtomicInteger count = new AtomicInteger();
        Consumer<String> consumer = mock(StringConsumer.class);
        Executor exec = Runnable::run; // execute inline

        WeakConsumers<String> consumers = new WeakConsumers<>(true);
        assertTrue(consumers.add((s) -> count.incrementAndGet())); // will immediately be eligible for GC
        assertTrue(consumers.add(consumer));
        System.gc();

        assertTrue(consumers.test(exec, "test"));
        assertEquals(0, count.get()); // weak link has been removed by GC

        verify(consumer).accept(any()); // consumer is still active due to normal reference
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void checkEntryEquals() {
        Consumer<String> consumer = mock(StringConsumer.class);
        WeakConsumers.Entry<String> lhs = new WeakConsumers.Entry<>(consumer);
        WeakConsumers.Entry<String> rhs = new WeakConsumers.Entry<>(consumer);
        assertEquals(lhs, rhs);
        lhs.clear();
        assertNotEquals(lhs, rhs);
        assertNotEquals(rhs, lhs);
        rhs.clear();
        assertEquals(lhs, rhs);
    }
}
