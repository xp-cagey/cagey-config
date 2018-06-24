package com.xpcagey.config.api;

import java.util.Collection;
import java.util.Collections;

public class PathDescriptor extends Descriptor {
    private final String path;

    public PathDescriptor(String provider, String path, String alias, boolean required) {
        super(provider, alias, required);
        this.path = path;
    }

    @Override public String getRawPath() { return this.path; }
    @Override public Collection<String> getResolverInputs() { return Collections.emptyList(); }
    @Override public Descriptor resolve(Config config) { return this; }
}
