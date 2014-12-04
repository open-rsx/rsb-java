package rsb.introspection;

import java.util.ArrayList;
import java.util.List;


public class PortableProcessInfo extends ProcessInfo {

    public PortableProcessInfo() {
        super();
    }

    @Override
    public int getPid() {
        return 1;
    }

    @Override
    public String getProgramName() {
        return "ManualIntrospectionTest";
    }

    @Override
    public List<String> getArguments() {
        return new ArrayList<String>();
    }

    @Override
    public long getStartTime() {
        return 10101010101010L;
    }

}
