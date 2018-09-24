package com.xpcagey.config.spi;

/**
 * ConfigServiceProvider is the binding point for the <code>cagey-config-core</code> to discover implementations of this
 * SPI; it is the thing that must be implemented to extend the configuration ecosystem. The path provided to
 * <code>load</code> is implementation specific; it is completely designed by the extension implementor. The use should
 * be documented in the extension package.
 */
public interface ConfigServiceProvider {
    String getName();
    ConfigSource load(ClassLoader loader, String path) throws IllegalPathException;
}
