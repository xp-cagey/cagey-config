package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;

public interface RawValueElement extends Element {
    boolean hasRawValue(Object value);
}
