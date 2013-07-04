package rsb.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import rsb.Event;

/**
 * @author jwienke
 */
public class DataCallbackTest {

    @Test
    public void invokeVoid() throws Throwable {

        class VoidCallback extends DataCallback<Void, Void> {

            @Override
            public Void invoke(final Void request) throws Throwable {
                return null;
            }

        }

        final Event result = new VoidCallback().internalInvoke(new Event(
                Void.class, null));
        assertEquals(Void.class, result.getType());
        assertNull(result.getData());

    }

    @Test
    public void invoke() throws Throwable {

        final String testString = "blaaaa";

        class MyCallback extends DataCallback<String, Integer> {

            @Override
            public String invoke(final Integer request) throws Throwable {
                return testString;
            }

        }

        final Event result = new MyCallback().internalInvoke(new Event(
                Integer.class, 42));
        assertEquals(String.class, result.getType());
        assertEquals(testString, result.getData());

    }

}
