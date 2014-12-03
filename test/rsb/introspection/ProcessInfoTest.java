package rsb.introspection;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProcessInfoTest {

    @Test
    public void testProcessInfo() {
        // TODO replace with non-os specific call to factory
        final LinuxProcessInfo info = new LinuxProcessInfo();
        // TODO add meaningful tests
        assertTrue(info.getPid()!=0);
        assertTrue(!info.getProgramName().isEmpty());
        assertTrue(info.arguments.size()!=0);
        assertTrue(info.startTime!=0);
    }

}
