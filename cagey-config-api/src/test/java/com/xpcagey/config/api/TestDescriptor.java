package com.xpcagey.config.api;

import java.util.Collection;
import java.util.Collections;

class TestDescriptor {
    static Descriptor create(String provider, String alias, String path, boolean required) {
        return new Descriptor(provider, alias, required) {
            public String getRawPath() { return path; }
            public Collection<String> getResolverInputs() { return Collections.emptyList(); }
            public Descriptor resolve(Config data) { return this; }
        };
    }
}
