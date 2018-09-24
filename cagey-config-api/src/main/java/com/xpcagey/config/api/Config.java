package com.xpcagey.config.api;

import java.util.Iterator;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Consumer;

public interface Config extends Iterable<Element>, AutoCloseable {
    /**
     * Returns the name of this configuration; useful if a program wants to have distinct configuration blocks that each
     * correspond to a different context.
     * @return the name of this configuration that was specified at load time
     */
    String getName();

    /**
     * Lists the items that were used to construct this configuration; useful for debugging
     * @return the sources for value lookup used by this config in priority order (highest to lowest)
     */
    Iterator<String> getSources();


    /**
     * Creates an artificial mapping of elements that start with the input path segment prefix; skips the prefix when
     * reporting names inside of the subtree. No wildcards or partial matches are supported; all segments of the path
     * prefix must be an exact match.  The subtree will correctly reflect any updates to the underlying root as they
     * occur.
     * @param prefix a dotted set of path segments to be used as an input filter
     * @return a Config that represents the subsection of configuration beginning with the prefix
     */
    Config subtree(String prefix);

    /**
     * Queries the configuration to see if it currently has an element at the specified key
     * @param key the key to search
     * @return true if the key exists
     */
    default boolean hasKey(String key) { return getOrNull(key) != null; }

    /**
     * Returns a snapshot of the current elements inside this configuration; if there were multiple descriptors used to
     * build a compound configuration the map will be flattened to show only the values that would be returned by
     * <code>get</code> queries.
     * @return the mapping of current values.
     */
    SortedMap<String, Element> getAll();

    /**
     * Attempt to retrieve an element by key in a type safe manner that allows functional chaining; best used when a
     * key is not required and the program needs to branch based on its presence
     * @param key the key to search
     * @return the element if found, or <code>Optional.empty()</code> if the element was not found.
     */
    default Optional<Element> get(String key) { return Optional.ofNullable(getOrNull(key)); }

    /**
     * Attempt to retrieve an element by key efficiently by not wrapping for type safety if it is missing; best used
     * in situations where temporary object creation overhead is not acceptable but the value is not required
     * @param key the key to search
     * @return the element if found, or <code>null</code> if the element was not found.
     */
    Element getOrNull(String key);

    /**
     * Attempt to retrieve an element by key without needing conditional logic to interpret the result; best used when
     * a default has been set for the key or when the program must have a value to operate correctly.
     * @param key the key to search
     * @return the element if found
     * @throws MissingElementException if the element was not found
     */
    default Element getOrThrow(String key) throws MissingElementException {
        Element e = getOrNull(key);
        if (e == null) throw new MissingElementException(getName(), key);
        return e;
    }

    /**
     * Places a change listener into the system. The listener will receive reports of any change in the current values
     * supplied by configuration.  This binding will *not* attempt to keep the callback from being garbage collected,
     * and is safe to use with ephemeral objects.
     * @param listener the code to be executed when a value updates.
     */
    void addListener(Consumer<Element> listener);

    /**
     * Removes a change listener from the system.
     * @param listener the code that was previously registered with <code>addListener</code>
     */
    void removeListener(Consumer<Element> listener);

    /**
     * Places a listener for a single key into the system.  If that key is already present, the config will immediately
     * fire the trigger, allowing initialization and update to share a common code path. This binding will *not* attempt
     * to keep the code from being garbage collected, and is safe to use with ephemeral objects. Triggers do not receive
     * an update if the key is missing or removed from the <code>Config</code>
     * @param key the key of the configuration parameter to track
     * @param trigger the code to be executed on register and again when the value updates
     */
    void addTrigger(String key, Consumer<Element> trigger);

    /**
     * Removes a listener for a single key from the system.
     * @param key the key of the configuration parameter to stop tracking
     * @param trigger the code that was previously registered with <code>addTrigger</code>
     */
    void removeTrigger(String key, Consumer<Element> trigger);
}
