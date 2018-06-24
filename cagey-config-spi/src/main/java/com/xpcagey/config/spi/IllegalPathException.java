package com.xpcagey.config.spi;

/**
 * IllegalPathException should be thrown by a ConfigServiceProvider if a requested path is malformed or unreachable. If
 * a resource is missing from the indicated path but the path itself is available, the ConfigServiceProvider should
 * return Optional.empty() instead of throwing an exception.
 */
public class IllegalPathException extends Exception {
    public final String path;
    public final String loader;
    public IllegalPathException(String loader, String path, Throwable cause) {
        super("Path ["+path+"] is not supported for loader ["+loader+"]", cause);
        this.loader = loader;
        this.path = path;
    }
}