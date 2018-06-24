package com.xpcagey.config.spi;

class DoClear implements ValueCommand {
    private final String key;

    DoClear(String key) { this.key = key; }

    @Override
    public void apply(ConfigSink sink) { sink.clear(key); }
}
