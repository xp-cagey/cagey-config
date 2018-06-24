package com.xpcagey.config.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;

public final class ConfigSystem {
    private ConfigSystem() {}

    private static Iterable<ConfigEngine> loader = ServiceLoader.load(ConfigEngine.class);
    private static final Object lock = new Object();
    private static volatile ConfigEngine engine;

    public static void setDefault(String key, boolean value) { bind().setDefault(key, value); }
    public static void setDefault(String key, Duration value) { bind().setDefault(key, value); }
    public static void setDefault(String key, Instant value) { bind().setDefault(key, value); }
    public static void setDefault(String key, float value) { bind().setDefault(key, value); }
    public static void setDefault(String key, int value) { bind().setDefault(key, value); }
    public static void setDefault(String key, String value) { bind().setDefault(key, value); }

    public static synchronized Config load(String name, Executor exec, Descriptor... descriptors) throws ConfigLoadException {
        return bind().load(name, exec, descriptors);
    }

    private static ConfigEngine bind() {
        ConfigEngine local = engine;
        if (local == null) {
            synchronized (lock) {
                local = engine;
                if (local == null)
                    engine = local = load();
            }
        }
        return local;
    }

    private static ConfigEngine load() {
        Iterator<ConfigEngine> engines = loader.iterator();
        if (!engines.hasNext())
            throw new IllegalStateException("No ConfigEngine has been found on the ClassPath");
        ConfigEngine found = engines.next();
        if (engines.hasNext())
            throw new IllegalStateException("Multiple ConfigEngines have been found on the ClassPath");
        return found;
    }

    // for unit test purposes only; this should not be used in production code.
    static void reset(Iterable<ConfigEngine> testLoader) {
        synchronized (lock) {
            loader = testLoader != null ? testLoader : ServiceLoader.load(ConfigEngine.class);
            engine = null;
        }
    }
}
