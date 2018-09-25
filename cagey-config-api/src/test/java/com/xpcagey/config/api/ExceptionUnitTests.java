package com.xpcagey.config.api;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

public class ExceptionUnitTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void illegalPathException() throws IllegalPathException {
        thrown.expect(IllegalPathException.class);
        thrown.expectMessage("Path [path-x] (raw-path-x) is not supported for provider [provider-x]");

        throw new IllegalPathException("provider-x", "path-x", "raw-path-x", new Exception());
    }

    @Test
    public void missingConfigException() throws MissingConfigException {
        Descriptor desc = new PathDescriptor("provider-x", "path-x", "alias-x", true);

        thrown.expect(MissingConfigException.class);
        thrown.expectMessage("Unable to find configuration for [alias-x=provider-x://path-x]");

        throw new MissingConfigException(desc);
    }

    @Test
    public void circularRequirementsException() throws CircularRequirementsException {
        Descriptor desc = new PathDescriptor("provider-x", "path-x", "alias-x", true);
        Descriptor desc2 = new PathDescriptor("provider-2", "path-2", "alias-2", true);

        thrown.expect(CircularRequirementsException.class);
        thrown.expectMessage("Found circular references while attempting to load [alias-x=provider-x://path-x], [alias-2=provider-2://path-2]");

        throw new CircularRequirementsException(Arrays.asList(desc, desc2));
    }


    @Test
    public void missingElementException() throws MissingElementException {
        thrown.expect(MissingElementException.class);
        thrown.expectMessage("Config [config-x] does not contain mandatory key [key-x]");

        throw new MissingElementException("config-x", "key-x");
    }

    @Test
    public void missingLoaderException() throws MissingLoaderException {
        thrown.expect(MissingLoaderException.class);
        thrown.expectMessage("Config provider [provider-x] is not supported; check your classpath and included jars");

        throw new MissingLoaderException("provider-x");
    }
}
