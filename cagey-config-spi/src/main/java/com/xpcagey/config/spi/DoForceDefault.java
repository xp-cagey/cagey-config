package com.xpcagey.config.spi;

class DoForceDefault implements ValueCommand {
    private final String key;

    DoForceDefault(String key) { this.key = key; }

    @Override public void apply(ConfigSink sink) { sink.forceDefault(key); }
}
