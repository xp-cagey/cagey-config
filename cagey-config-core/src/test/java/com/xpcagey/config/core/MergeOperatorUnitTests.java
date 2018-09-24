package com.xpcagey.config.core;

import com.xpcagey.config.api.Element;
import com.xpcagey.config.element.RawValueElement;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.junit.Test;

import java.util.concurrent.ConcurrentMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MergeOperatorUnitTests {
    @Test
    public void shouldSwapNullWithNullWithoutUpdate() {
        final RawValueElement element = mock(RawValueElement.class);
        final ConfigEventBindings<RawValueElement> bindings = mock(RawValueElementConfigEventBindings.class);
        final ConcurrentMap<String, ImmutableSortedElementSet> map = mock(ElementConcurrentMap.class);
        when(map.get(anyString())).thenReturn(null);

        MergeOperator op = new MergeOperator(map, bindings) {
            @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
                return null;
            }
        };

        op.apply("", 0, element);
        verify(map).get(anyString());
        verifyNoMoreInteractions(element, bindings, map);
    }

    @Test
    public void shouldSwapValueWithNull() {
        final RawValueElement element = mock(RawValueElement.class);
        final ImmutableSortedElementSet set = mock(ImmutableSortedElementSet.class);
        final ConfigEventBindings<RawValueElement> bindings = mock(RawValueElementConfigEventBindings.class);
        final ConcurrentMap<String, ImmutableSortedElementSet> map = mock(ElementConcurrentMap.class);
        when(map.get(anyString())).thenReturn(set);
        when(map.remove(anyString(),any())).thenReturn(false, true); // force it to make 2 attempts

        MergeOperator op = new MergeOperator(map, bindings) {
            @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
                return null;
            }
        };

        op.apply("", 0, element);
        verify(map, times(2)).get(anyString());
        verify(map, times(2)).remove(anyString(),any());
        verify(bindings).notifyRemoved(anyString());
        verifyNoMoreInteractions(element, bindings, map);
    }

    @Test
    public void shouldSwapNullWithValue() {
        final RawValueElement element = mock(RawValueElement.class);
        final ImmutableSortedElementSet set = mock(ImmutableSortedElementSet.class);
        final ConfigEventBindings<RawValueElement> bindings = mock(RawValueElementConfigEventBindings.class);
        final ConcurrentMap<String, ImmutableSortedElementSet> map = mock(ElementConcurrentMap.class);
        when(map.get(anyString())).thenReturn(null);
        when(map.putIfAbsent(anyString(),any())).thenReturn(set, null); // force it to make 2 attempts

        MergeOperator op = new MergeOperator(map, bindings) {
            @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
                return set;
            }
        };

        op.apply("", 0, element);
        verify(map, times(2)).get(anyString());
        verify(map, times(2)).putIfAbsent(anyString(),any());
        verify(bindings).notify(any());
        verifyNoMoreInteractions(element, bindings, map);
    }

    @Test
    public void shouldAllowSameValue() {
        final RawValueElement element = mock(RawValueElement.class);
        final ImmutableSortedElementSet set = mock(ImmutableSortedElementSet.class);
        when(set.matches(any())).thenReturn(true);
        final ConfigEventBindings<RawValueElement> bindings = mock(RawValueElementConfigEventBindings.class);
        final ConcurrentMap<String, ImmutableSortedElementSet> map = mock(ElementConcurrentMap.class);
        when(map.get(anyString())).thenReturn(set);
        when(map.replace(anyString(), any(), any())).thenReturn(false, true); // force retry

        MergeOperator op = new MergeOperator(map, bindings) {
            @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
                return prev;
            }
        };

        op.apply("", 0, element);
        verify(map, times(2)).get(anyString());
        verify(map, times(2)).replace(anyString(),any(),any());
        verifyNoMoreInteractions(element, bindings, map); // no notification due to match before/after
    }

    @Test
    public void shouldSwapValueWithValue() {
        final RawValueElement element = mock(RawValueElement.class);
        final ImmutableSortedElementSet set = mock(ImmutableSortedElementSet.class);
        when(set.matches(any())).thenReturn(false);
        final ImmutableSortedElementSet set2 = mock(ImmutableSortedElementSet.class);
        final ConfigEventBindings<RawValueElement> bindings = mock(RawValueElementConfigEventBindings.class);
        final ConcurrentMap<String, ImmutableSortedElementSet> map = mock(ElementConcurrentMap.class);
        when(map.get(anyString())).thenReturn(set);
        when(map.replace(anyString(), any(), any())).thenReturn(false, true); // force retry

        MergeOperator op = new MergeOperator(map, bindings) {
            @Override protected ImmutableSortedElementSet execute(ImmutableSortedElementSet prev, int score, RawValueElement element) {
                return set2;
            }
        };

        op.apply("", 0, element);
        verify(map, times(2)).get(anyString());
        verify(map, times(2)).replace(anyString(),any(),any());
        verify(bindings).notify(any());
        verifyNoMoreInteractions(element, bindings, map); // no notification due to match before/after
    }
}
