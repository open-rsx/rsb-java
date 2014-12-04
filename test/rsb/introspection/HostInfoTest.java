package rsb.introspection;

import java.util.logging.Logger;

import org.junit.Test;


public class HostInfoTest {

    private static final Logger LOG = Logger.getLogger(HostInfoTest.class.getName());

    @Test
    public void testGetMachineType() {
        final PortableHostInfo portableHostInfo = new PortableHostInfo();
        LOG.info("Detected " + portableHostInfo.getMachineType().name() + " machine type.");
    }

}
