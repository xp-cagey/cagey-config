package com.xpcagey.config.spi;

import java.time.Instant;

class DoSetInstant implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient Instant value;

    DoSetInstant(String key, boolean isSensitive, Instant value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
