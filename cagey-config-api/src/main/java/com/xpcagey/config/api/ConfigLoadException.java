package com.xpcagey.config.api;

/**
 * ConfigLoadException is a common ancestor for loader exceptions that is provided for convenience
 */
public abstract class ConfigLoadException extends Exception {
    ConfigLoadException(String message) { super(message); }
    ConfigLoadException(String message, Throwable cause) { super(message, cause); }
}
