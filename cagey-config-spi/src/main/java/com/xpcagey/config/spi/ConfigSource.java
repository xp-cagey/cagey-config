package com.xpcagey.config.spi;

import java.util.function.Consumer;

/**
 * ConfigSource represents an arbitrary mutable, strongly typed value store; it is designed to allow loose coupling
 * with cagey-config systems by passing update commands on change.  For the sake of efficiency the source must also be
 * able of initializing a target directly without creating a command list. Separation of the implementation of Element
 * from this SPI is intentional since it narrows the expectations of implementors and allows the configuration core
 * package to support new features without requiring an update from the various source packages.
 */
public interface ConfigSource extends Iterable<ValueCommand>, AutoCloseable {
    String getPath();
    void initialize(ConfigSink sink);
    void register(Consumer<ValueCommand> event);
    void unregister(Consumer<ValueCommand> event);
}
