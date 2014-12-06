package rsb.introspection;

import java.util.List;

/**
 * Interface for process information model classes.
 *
 * @author swrede
 *
 */
public interface ProcessInfo {

    public abstract int getPid();

    public abstract String getProgramName();

    public abstract List<String> getArguments();

    public abstract long getStartTime();

    public abstract String getUserName();

}
