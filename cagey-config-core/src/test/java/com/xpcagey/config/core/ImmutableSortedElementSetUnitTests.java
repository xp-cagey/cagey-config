package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.BooleanElement;
import com.xpcagey.config.element.RawValueElement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImmutableSortedElementSetUnitTests {
    @Test
    public void shouldConstructProperly() {
        RawValueElement e = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(0, e);
        assertEquals(e, set.element());
    }

    @Test
    public void shouldAddToFrontProperlyOnReplace() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);
        set = set.addOrReplace(0, e0);
        assertEquals(e0, set.element());
    }

    @Test
    public void shouldAddToFrontProperlyOnIgnore() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);
        set = set.addOrIgnore(0, e0);
        assertEquals(e0, set.element());
    }

    @Test
    public void shouldRemoveFromFrontProperly() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);
        set = set.addOrReplace(0, e0);
        set = set.remove(0);
        assertEquals(e1, set.element());
    }

    @Test
    public void shouldIgnoreRemoveMissingProperly() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);
        set = set.addOrReplace(0, e0);
        assertEquals(set, set.remove(2));
    }

    @Test
    public void shouldReturnNullOnRemoveLast() {
        RawValueElement e1 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);
        set = set.remove(1);
        assertNull(set);
    }

    @Test
    public void shouldReplaceFrontProperly() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e0_2 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(0, e0);
        set = set.addOrReplace(0, e0_2);
        assertEquals(e0_2, set.element());
    }

    @Test
    public void shouldOrderProperly() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        RawValueElement e2 = mock(RawValueElement.class);
        RawValueElement e2_2 = mock(RawValueElement.class);
        RawValueElement e3 = mock(RawValueElement.class);
        ImmutableSortedElementSet set = new ImmutableSortedElementSet(1, e1);

        set = set.addOrReplace(3, e3);
        set = set.addOrReplace(0, e0);
        // test both branches of addOrReplace
        set = set.addOrReplace(2, e2);
        set = set.addOrReplace(2, e2_2);
        set = set.remove(0);
        assertEquals(e1, set.element());
        set = set.remove(1);
        assertEquals(e2_2, set.element());
        set = set.remove(2);
        assertEquals(e3, set.element());

        // test both branches of addOrIgnore
        set = set.addOrIgnore(2, e2_2);
        set = set.addOrIgnore(2, e2);
        assertEquals(e2_2, set.element());
    }

    @Test
    public void shouldReportMatchesCorrectly() {
        RawValueElement e0 = new BooleanElement("key", false, false);
        RawValueElement e1 = new BooleanElement("key", false, false);
        RawValueElement e2 = new BooleanElement("key2", false, false);
        RawValueElement e3 = new BooleanElement("key", true, false);
        RawValueElement e4 = new BooleanElement("key", false, true);
        ImmutableSortedElementSet set0 = new ImmutableSortedElementSet(0, e0);
        ImmutableSortedElementSet set1 = new ImmutableSortedElementSet(0, e1);
        ImmutableSortedElementSet set2 = new ImmutableSortedElementSet(0, e2);
        ImmutableSortedElementSet set3 = new ImmutableSortedElementSet(0, e3);
        ImmutableSortedElementSet set4 = new ImmutableSortedElementSet(0, e4);
        assertFalse(set0.matches(null));
        assertTrue(set0.matches(set0));
        assertTrue(set0.matches(set1));
        assertFalse(set0.matches(set2));
        assertFalse(set0.matches(set3));
        assertFalse(set0.matches(set4));
    }

    @Test
    public void shouldProvideIteratorBridgeToElement() {
        RawValueElement e0 = mock(RawValueElement.class);
        RawValueElement e1 = mock(RawValueElement.class);
        List<ImmutableSortedElementSet> set = new ArrayList<>();
        set.add(new ImmutableSortedElementSet(1, e1));
        set.add(new ImmutableSortedElementSet(0, e0));

        Iterator<Element> it = new ImmutableSortedElementSet.Iterator(set.iterator());
        assertEquals(e1, it.next());
        assertEquals(e0, it.next());
        assertFalse(it.hasNext());
    }
}
