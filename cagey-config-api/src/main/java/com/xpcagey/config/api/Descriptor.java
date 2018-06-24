package com.xpcagey.config.api;

import java.util.Collection;

public abstract class Descriptor implements Comparable<Descriptor> {
    private final String provider;
    private final String alias;
    private final boolean required;
    protected Descriptor(String provider, String alias, boolean required) {
        this.provider = provider;
        this.alias = alias;
        this.required = required;
    }

    public String getProvider() { return this.provider; }
    public String getAlias() { return this.alias; }
    public boolean isRequired() { return this.required; }

    public abstract String getRawPath();
    public abstract Collection<String> getResolverInputs();
    public abstract Descriptor resolve(Config data) throws ConfigLoadException;

    @Override
    public int compareTo(Descriptor o) {
        int comp = getAlias().compareTo(o.getAlias());
        if (comp != 0)
            return comp;
        comp = getProvider().compareTo(o.getProvider());
        if (comp != 0)
            return comp;
        return getRawPath().compareTo(o.getRawPath());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Descriptor) {
            return equals((Descriptor)o);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {  return toString().hashCode(); }

    @Override
    public String toString() {
        return alias+"="+provider+"://"+getRawPath();
    }

    private boolean equals(Descriptor o) {
        return provider.equals(o.provider) && alias.equals(o.alias) && getRawPath().equals(o.getRawPath());
    }

}
