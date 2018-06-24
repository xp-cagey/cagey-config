package com.xpcagey.config.api;

public class MissingResolverFieldException extends ConfigLoadException {
    public MissingResolverFieldException(MissingElementException e) {
        super(e.getMessage(), e);
    }
}
