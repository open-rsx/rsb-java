package rsb.transport;

import org.junit.Test;

/**
 * Test for {@link DefaultTransports}.
 *
 * @author jwienke
 */
public class DefaultTransportsTest {

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void duplicatedRegistration() {
        DefaultTransports.register();
        DefaultTransports.register();
    }

}
