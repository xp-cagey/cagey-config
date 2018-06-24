package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;

import java.util.Arrays;

class ImmutableSortedElementSet {
    private final Entry[] entries;

    ImmutableSortedElementSet(int score, RawValueElement e) {
        this.entries = new Entry[]{new Entry(score, e)};
    }

    private ImmutableSortedElementSet(Entry[] entries) {
        this.entries = entries;
    }

    RawValueElement element() { return entries[0].element; }

    boolean matches(ImmutableSortedElementSet o) {
        if (o == null) return false;
        if (o == this) return true;
        RawValueElement oe = o.element();
        RawValueElement e = element();
        return e.isSensitive() == oe.isSensitive() &&
                e.getKey().equals(oe.getKey()) &&
                e.hasEqualValue(oe);
    }

    // new value wins; assumes a single steam of inputs should be dominant
    ImmutableSortedElementSet addOrReplace(int score, RawValueElement e) {
        final Entry entry = new Entry(score, e);
        int index = Arrays.binarySearch(entries, entry);
        if (index < 0)
            return add(~index, entry);

        final Entry[] copy = new Entry[entries.length];
        System.arraycopy(entries, 0, copy, 0, entries.length);
        copy[index] = entry;
        return new ImmutableSortedElementSet(copy);
    }

    // old value wins; used to prevent initialization from overwriting authoritative event update stream
    ImmutableSortedElementSet addOrIgnore(int score, RawValueElement e) {
        final Entry entry = new Entry(score, e);
        int index = Arrays.binarySearch(entries, entry);
        if (index < 0)
            return add(~index, entry);

        return this;
    }

    ImmutableSortedElementSet remove(int score) {
        final Entry entry = new Entry(score, null);
        int index = Arrays.binarySearch(entries, entry);
        if (index < 0)
            return this;
        if (entries.length == 1)
            return null;
        final Entry[] copy = new Entry[entries.length-1];
        System.arraycopy(entries, 0, copy, 0, index);
        System.arraycopy(entries, index+1, copy, index, entries.length-(index+1));
        return new ImmutableSortedElementSet(copy);
    }

    private ImmutableSortedElementSet add(int index, Entry entry) {
        final Entry[] copy = new Entry[entries.length+1];
        System.arraycopy(entries, 0, copy, 0, index);
        copy[index] = entry;
        if (index < entries.length)
            System.arraycopy(entries, index, copy, index+1, entries.length-index);
        return new ImmutableSortedElementSet(copy);
    }

    static class Entry implements Comparable<Entry> {
        private final int score;
        private final RawValueElement element;
        Entry(int score, RawValueElement element) {
            this.score = score;
            this.element = element;
        }

        @Override public int compareTo(Entry o) {
            return Integer.compare(score, o.score);
        }
    }

    static class Iterator implements java.util.Iterator<Element> {
        private final java.util.Iterator<ImmutableSortedElementSet> it;
        Iterator(java.util.Iterator<ImmutableSortedElementSet> it) { this.it = it; }

        @Override public boolean hasNext() { return it.hasNext(); }
        @Override public Element next() {
            ImmutableSortedElementSet set = it.next();
            return set.element();
        }
    }
}