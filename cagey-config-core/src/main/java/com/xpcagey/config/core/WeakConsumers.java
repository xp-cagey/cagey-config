package com.xpcagey.config.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class WeakConsumers<T> implements BiPredicate<Executor, T> {
    private final HashMap<Entry<? super T>, Boolean> mapping = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rLock = rwLock.readLock();
    private final Lock wLock = rwLock.writeLock();
    private final boolean shouldExpire;
    private boolean expired = false;

    WeakConsumers(boolean shouldExpire) {
        this.shouldExpire = shouldExpire;
    }

    @Override public boolean test(Executor exec, T t) {
        boolean dirty = false;
        rLock.lock();
        Set<WeakConsumers.Entry<? super T>> items;
        try {
            if (expired)
                return false;
            items = new HashSet<>(mapping.keySet());
        } finally {
            rLock.unlock();
        }
        for(Entry<? super T> e : items) {
            if (!e.apply(exec, t)) dirty = true;
        }
        if (!dirty) return true;
        wLock.lock();
        try {
            return !checkExpired();
        } finally {
            wLock.unlock();
        }
    }

    boolean add(Consumer<? super T> element) {
        wLock.lock();
        try {
            if (expired)
                return false;
            mapping.put(new Entry<>(element), true);
            return true;
        } finally {
            wLock.unlock();
        }
    }

    boolean remove(Consumer<? super T> element) {
        wLock.lock();
        try {
            mapping.remove(new Entry<>(element), true);
            return checkExpired();
        } finally {
            wLock.unlock();
        }
    }

    private boolean checkExpired() {
        if (!expired) {
            mapping.entrySet().removeIf(e -> e.getKey().get() == null);
            expired = shouldExpire && mapping.isEmpty();
        }
        return expired;
    }

    // package level access for testing only
    void clear() {
        mapping.entrySet().clear();
        if (shouldExpire) expired = true;
    }

    // package level access for testing only
    static class Entry<Q> extends WeakReference<Consumer<Q>> {
        private final int hash;
        Entry(Consumer<Q> c) {
            super(c);
            this.hash = c.hashCode();
        }

        @Override public int hashCode() { return hash; }
        @Override public boolean equals(Object o) {
            Consumer<?> value = get();
            if (o instanceof Entry<?>) {
                Consumer<?> other = ((Entry<?>) o).get();
                if (value == null)
                    return other == null;
                return value.equals(other);
            }
            return false;
        }

        boolean apply(Executor exec, Q value) {
            Consumer<Q> func = get();
            if (func == null)
                return false;
            exec.execute(() -> func.accept(value));
            return true;
        }
    }
}
