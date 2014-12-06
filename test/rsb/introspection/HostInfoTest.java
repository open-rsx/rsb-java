package rsb.introspection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import rsb.util.OSDetector;


public class HostInfoTest {

    private static final Logger LOG = Logger.getLogger(HostInfoTest.class.getName());

    @Test
    public void testHostInfo() {
        final HostInfo info;
        switch (OSDetector.getOSFamily()) {
        case LINUX:
            info = new LinuxHostInfo();
            break;
        default:
            info = new PortableHostInfo();
            break;
        }
        // TODO add meaningful tests
        assertFalse(info.getHostname().isEmpty());
        LOG.log(Level.INFO, "Hostname is: " + info.getHostname());
        assertFalse(info.getId().isEmpty());
        LOG.log(Level.INFO, "Machine ID is: " + info.getId());
        assertNotNull(info.getMachineType());
        LOG.log(Level.INFO, "Machine type is: " + info.getMachineType().name());
        assertNotNull(info.getSoftwareType());
        LOG.log(Level.INFO, "Software type is: " + info.getSoftwareType().name());
    }

}
