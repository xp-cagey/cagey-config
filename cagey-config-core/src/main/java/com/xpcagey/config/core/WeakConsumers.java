package com.xpcagey.config.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class WeakConsumers<T> implements BiPredicate<Executor, T> {
    private final HashMap<Entry<T>, Boolean> mapping = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock rLock = rwLock.readLock();
    private final Lock wLock = rwLock.writeLock();
    private final boolean shouldExpire;
    private boolean expired = false;

    WeakConsumers(boolean shouldExpire) {
        this.shouldExpire = shouldExpire;
    }

    @Override public boolean test(Executor exec, T t) {
        rLock.lock();
        try {
            boolean dirty = false;
            if (expired)
                return false;
            for(Entry<T> e : mapping.keySet()) {
                if (!e.apply(exec, t)) dirty = true;
            }
            if (!dirty) return true;
        } finally {
            rLock.unlock();
        }
        wLock.lock();
        try {
            return !checkExpired();
        } finally {
            wLock.unlock();
        }
    }

    boolean add(Consumer<T> element) {
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

    boolean remove(Consumer<T> element) {
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
    static class Entry<T> extends WeakReference<Consumer<T>> {
        private int hash;
        Entry(Consumer<T> c) {
            super(c);
            this.hash = c.hashCode();
        }

        @Override public int hashCode() { return hash; }
        @Override public boolean equals(Object o) {
            Consumer<?> value = get();
            Consumer<?> other = ((Entry<?>)o).get();
            if(value == null)
                return other == null;
            return value.equals(other);
        }

        boolean apply(Executor exec, T value) {
            Consumer<T> func = get();
            if (func == null)
                return false;
            exec.execute(() -> func.accept(value));
            return true;
        }
    }
}
