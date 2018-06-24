package com.xpcagey.config.api;

public class MissingElementException extends Exception {
    public final String configName;
    public final String key;
    public MissingElementException(String configName, String key) {
        super("Config ["+configName+"] does not contain mandatory key ["+key+"]");
        this.configName = configName;
        this.key = key;
    }
}
