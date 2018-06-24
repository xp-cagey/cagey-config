package com.xpcagey.config.api;

import java.util.Arrays;
import java.util.Collection;

public class KeyedDescriptor extends Descriptor {
    private static final String DELIMITER = "::";
    private final String key;
    private final String[] sources;

    public KeyedDescriptor(String provider, String key, String alias, boolean required, String... sources) {
        super(provider, alias, required);
        this.sources = sources;
        this.key = key;
    }

    @Override public String getRawPath() { return this.key + "@" + String.join(DELIMITER, sources); }
    @Override public Collection<String> getResolverInputs() { return Arrays.asList(this.sources); }
    @Override public Descriptor resolve(Config config) throws MissingResolverFieldException {
        String path;
        String provider = getProvider();
        try {
            path = config.getOrThrow(this.key).getAsString();
            return new PathDescriptor(provider, path, getAlias(), isRequired());
        } catch (MissingElementException e) {
            throw new MissingResolverFieldException(e);
        }
    }
}