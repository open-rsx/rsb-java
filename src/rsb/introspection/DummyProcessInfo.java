package rsb.introspection;

import java.util.ArrayList;
import java.util.List;


public class DummyProcessInfo extends ProcessInfo {

    @Override
    public int getPid() {
        return 1;
    }

    @Override
    public String getProgramName() {
        return "IntrospectionTest";
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
