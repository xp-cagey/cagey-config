package com.xpcagey.config.api;

/**
 * IllegalPathException should be thrown by a ConfigEngine if a requested path is malformed or unreachable. If
 * a resource is missing from the indicated path but the path itself is available, the ConfigServiceProvider should
 * return Optional.empty() instead of throwing an exception.
 */
public class IllegalPathException extends ConfigLoadException {
    public final String path;
    public final String rawPath;
    public final String provider;
    public IllegalPathException(String provider, String path, String rawPath, Throwable cause) {
        super("Path ["+path+"] ("+rawPath+") is not supported for provider ["+provider+"]", cause);
        this.provider = provider;
        this.path = path;
        this.rawPath = path;
    }
}