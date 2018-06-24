package com.xpcagey.config.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CircularRequirementsException extends ConfigLoadException {
    public final Set<Descriptor> desc;
    public CircularRequirementsException(Collection<Descriptor> desc) {
        super(buildMessage(desc));
        this.desc = new HashSet<>(desc);
    }

    private static String buildMessage(Collection<Descriptor> desc) {
        StringBuilder builder = new StringBuilder();
        for(Descriptor d : desc) {
            if (builder.length() == 0)
                builder.append("Found circular references while attempting to load [");
            else
                builder.append("], [");
            builder.append(d.toString());
        }
        builder.append("]");
        return builder.toString();
    }
}
