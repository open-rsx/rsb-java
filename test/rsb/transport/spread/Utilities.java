package rsb.transport.spread;

import rsb.util.Properties;

/**
 * A class with helpers for testing the spread transport.
 *
 * @author jwienke
 */
public final class Utilities {

    private Utilities() {
        super();
        // prevent initialization of helper class
    }

    public static SpreadWrapper createSpreadWrapper() throws Throwable {
        return new SpreadWrapper("localhost", new Properties().load()
                .getProperty("transport.spread.port", "4803").asInteger(), true);
    }

}
