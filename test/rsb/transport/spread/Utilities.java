package rsb.transport.spread;

import rsb.util.ConfigLoader;
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
        final ConfigLoader loader = new ConfigLoader();
        return new SpreadWrapper("localhost", loader.load(new Properties())
                .getProperty("transport.spread.port", "4803").asInteger(), true);
    }

}
