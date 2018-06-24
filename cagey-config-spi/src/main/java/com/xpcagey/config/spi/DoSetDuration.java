package com.xpcagey.config.spi;

import java.time.Duration;

class DoSetDuration implements ValueCommand {
    private final String key;
    private final boolean isSensitive;
    private final transient Duration value;

    DoSetDuration(String key, boolean isSensitive, Duration value) {
        this.key = key;
        this.value = value;
        this.isSensitive = isSensitive;
    }

    @Override
    public void apply(ConfigSink sink) { sink.set(key, isSensitive, value); }
}
