package rsb.introspection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import rsb.util.OSDetector;

public class ProcessInfoTest {

    private static final Logger LOG = Logger.getLogger(ProcessInfoTest.class.getName());

    @Test
    public void testProcessInfo() {
        final ProcessInfo info;
        switch (OSDetector.getOSFamily()) {
        case LINUX:
            info = new LinuxProcessInfo();
            LOG.info("Created LinuxProcessInfo");
            break;
        default:
            info = new PortableProcessInfo();
            LOG.info("Created PortableProcessInfo");
            break;
        }
        // TODO add meaningful tests
        assertFalse(info.getUserName().isEmpty());
        LOG.log(Level.INFO, "User is: " + info.getUserName());
        assertTrue(info.getPid()!=0);
        LOG.log(Level.INFO, "PID is: " + info.getPid());
        assertTrue(!info.getProgramName().isEmpty());
        LOG.log(Level.INFO, "ProgramName is: " + info.getProgramName());
        assertTrue(info.getArguments().size()!=0);
        LOG.log(Level.INFO, "Arguments are: " + info.getArguments());
        assertTrue(info.getStartTime()!=0);
        LOG.log(Level.INFO, "Starttime is: " + info.getStartTime());
    }

}
