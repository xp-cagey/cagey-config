package com.xpcagey.config.spi;

class DoSetString implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient String value;

    DoSetString(String key, boolean isSensitive, String value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
