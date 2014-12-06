package rsb.introspection;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PortableProcessInfo extends CommonProcessInfo {

    private static final Logger LOG = Logger.getLogger(PortableProcessInfo.class.getName());

    public PortableProcessInfo() {
        super();
        this.initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // before the @ symbol is the PID.
        final String jvmName = runtime.getName();
        try {
            this.pid = Integer.valueOf(jvmName.split("@")[0]);
        } catch (final NumberFormatException e) {
           LOG.log(Level.INFO, "Exception when parsing pid (RuntimeMXBean.getName()==" + jvmName + ")", e);
        }

        this.startTime = runtime.getStartTime();

        this.name = "java-" + System.getProperty("java.runtime.version");
        // Returns the input arguments passed to the Java virtual machine which does
        // not include the arguments to the main method. This method returns an empty
        // list if there is no input argument to the Java virtual machine.
        this.arguments = runtime.getInputArguments();
    }

}
