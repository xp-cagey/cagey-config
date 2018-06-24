package com.xpcagey.config.spi;

class DoSetLong implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient long value;

    DoSetLong(String key, boolean isSensitive, long value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
