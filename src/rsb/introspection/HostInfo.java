package rsb.introspection;

import rsb.util.OSFamily;


/**
 * Interface for host information model classes.
 *
 * @author swrede
 *
 */
public interface HostInfo {

    public enum MachineType {
        x86,
        x86_64,
        UNKNOWN
    }

    public abstract String getId();

    public abstract String getHostname();

    public abstract OSFamily getSoftwareType();

    public abstract MachineType getMachineType();

}
