package com.xpcagey.config.element;

import com.xpcagey.config.api.Element;

public class ResetToDefaultElement extends BaseElement<Object> {
    public ResetToDefaultElement(String key) {
        super(key, false, null);
    }
    @Override public boolean hasRawValue(Object value) {
        return DefaultManagement.get(getKey()).hasRawValue(value);
    }
    @Override public boolean hasEqualValue(Element other) {
        if (other instanceof ResetToDefaultElement)
            return other.getKey().equals(getKey());
        if (other == null)
            return false;
        return other.hasEqualValue(this);
    }
}
