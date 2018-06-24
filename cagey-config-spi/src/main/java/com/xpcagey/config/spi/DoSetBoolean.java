package com.xpcagey.config.spi;

class DoSetBoolean implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient boolean value;

    DoSetBoolean(String key, boolean isSensitive, boolean value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
