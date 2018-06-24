package com.xpcagey.config.api;

public class MissingLoaderException extends ConfigLoadException {
    public final String provider;
    public MissingLoaderException(String provider) {
        super("Config provider ["+provider+"] is not supported; check your classpath and included jars");
        this.provider = provider;
    }
}
