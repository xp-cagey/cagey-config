package com.xpcagey.config.spi;

class DoSetDouble implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient double value;

    DoSetDouble(String key, boolean isSensitive, double value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
