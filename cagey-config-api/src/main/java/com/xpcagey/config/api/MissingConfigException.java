package com.xpcagey.config.api;

public class MissingConfigException extends ConfigLoadException {
    public final Descriptor desc;
    public MissingConfigException(Descriptor desc) {
        super("Unable to find configuration for ["+desc.toString()+"]");
        this.desc = desc;
    }
}
