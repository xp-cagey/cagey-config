package com.xpcagey.config.core;

import com.xpcagey.config.api.*;
import com.xpcagey.config.element.DefaultManagement;
import com.xpcagey.config.spi.ConfigServiceProvider;
import com.xpcagey.config.spi.ConfigSource;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;

public class ConfigEngine implements com.xpcagey.config.api.ConfigEngine {
    private final ServiceLoader<ConfigServiceProvider> loader = ServiceLoader.load(ConfigServiceProvider.class);
    private final Map<Descriptor, ConfigModule> modules = new HashMap<>();
    private final Map<String, ConfigServiceProvider> providers = new HashMap<>();

    @Override public void setDefault(String key, boolean value) { DefaultManagement.set(key, value); }
    @Override public void setDefault(String key, double value) { DefaultManagement.set(key, value); }
    @Override public void setDefault(String key, Duration value) { DefaultManagement.set(key, value); }
    @Override public void setDefault(String key, Instant value) { DefaultManagement.set(key, value); }
    @Override public void setDefault(String key, long value) { DefaultManagement.set(key, value); }
    @Override public void setDefault(String key, String value) { DefaultManagement.set(key, value); }

    @Override
    public Config load(String name, ClassLoader classLoader, Executor exec, Descriptor... descriptors) throws ConfigLoadException {
        synchronized(providers) {
            // look up our available providers
            if (providers.isEmpty()) {
                for (ConfigServiceProvider csp : loader)
                    providers.put(csp.getName(), csp);
            }
            Set<String> resolvedAndMissing = new HashSet<>();
            Map<String, ConfigModule> byAlias = new HashMap<>();

            while (resolvedAndMissing.size() < descriptors.length) {
                boolean progress = false;
                for (Descriptor d : descriptors) {
                    String alias = d.getAlias();
                    if (resolvedAndMissing.contains(alias))
                        continue;
                    if (!resolvedAndMissing.containsAll(d.getResolverInputs()))
                        continue;

                    progress = true;
                    Descriptor resolved = resolve(exec, d, byAlias);
                    Optional<ConfigModule> module = Optional.ofNullable(modules.get(resolved));
                    if (!module.isPresent())
                        module = load(classLoader, d).map(src -> {
                            ConfigModule found = new ConfigModule(exec, d, src);
                            modules.put(d, found);
                            return found;
                        });
                    resolvedAndMissing.add(alias);
                    module.ifPresent(m -> byAlias.put(alias, m));
                }
                if (!progress)
                    throw new CircularRequirementsException(reportDeadlock(resolvedAndMissing, byAlias, Arrays.asList(descriptors)));
            }

            // attempt to order items, dropping optional ones and ensuring we have all requirements met
            ModularConfig cfg = new ModularConfig(exec, name);
            for(Descriptor d : descriptors) {
                ConfigModule m = byAlias.get(d.getAlias());
                if (m != null)
                    cfg.append(m);
            }
            return cfg;
        }
    }

    private Descriptor resolve(Executor exec, Descriptor d, Map<String, ConfigModule> byAlias) throws ConfigLoadException {
        try (ModularConfig cfg = new ModularConfig(exec, "")) {
            for (String input : d.getResolverInputs()) {
                ConfigModule module = byAlias.get(input);
                if (module != null) cfg.append(module);
            }
            return d.resolve(cfg);
        }
    }

    private Optional<ConfigSource> load(ClassLoader classLoader, Descriptor d) throws ConfigLoadException {
        try {
            ConfigServiceProvider csp = providers.get(d.getProvider());
            if (csp != null)
                return Optional.of(csp.load(classLoader, d.getRawPath()));
            else if (d.isRequired())
                throw new MissingLoaderException(d.getProvider());
            return Optional.empty();
        } catch (com.xpcagey.config.spi.IllegalPathException e) {
            throw new IllegalPathException(e.loader, e.path, d.getRawPath(), e);
        }
    }

    private Collection<Descriptor> reportDeadlock(Set<String> resolvedAndMissing, Map<String, ConfigModule> byAlias, Collection<Descriptor> descriptors) throws ConfigLoadException {
        Collection<String> included = new HashSet<>(resolvedAndMissing);
        for (Descriptor d: descriptors) {
            included.add(d.getAlias());
        }
        for (Descriptor d: descriptors) {
            if (!included.containsAll(d.getResolverInputs()))
                throw new MissingConfigException(d);
        }
        Collection<Descriptor> circular = new HashSet<>(descriptors);
        circular.removeIf(d -> byAlias.containsKey(d.getAlias()));
        return circular;
    }
}
