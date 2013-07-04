package rsb.patterns;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.Scope;

/**
 * @author jwienke
 */
public class RpcIntegrationTest {

    private static final String TEST_DATA = "A test String";
    private static final String METHOD_NAME = "myMethod";
    private static final String VOID_VOID_METHOD_NAME = "voidvoid";

    private LocalServer server;
    private RemoteServer remote;

    @Before
    public void setUp() throws Throwable {

        final Scope scope = new Scope("/example/server");
        this.server = Factory.getInstance().createLocalServer(scope);
        this.server.addMethod(METHOD_NAME, new ReplyDataCallback());
        this.server.addMethod(VOID_VOID_METHOD_NAME,
                new DataCallback<Void, Void>() {

                    @Override
                    public Void invoke(final Void request) throws Throwable {
                        return null;
                    }

                });
        this.server.activate();
        this.remote = Factory.getInstance().createRemoteServer(scope);
        this.remote.activate();

    }

    @After
    public void tearDown() throws Throwable {
        if (this.server != null) {
            try {
                this.server.deactivate();
            } catch (final Throwable t) {
                // we cannot do anything here in such a case
            }
        }
        if (this.remote != null) {
            try {
                this.remote.deactivate();
            } catch (final Throwable t) {
                // we cannot do anything here in such a case
            }
        }
    }

    @Test
    public void roundtripSmoke() throws Throwable {
        for (int i = 0; i < 100; i++) {
            final String reply = this.remote.call(METHOD_NAME, TEST_DATA);
            assertEquals(reply, TEST_DATA);
        }
    }

    @Test
    public void roundtripEventSyntax() throws Throwable {
        final Event reply = this.remote.call(METHOD_NAME, new Event(
                String.class, TEST_DATA));
        assertEquals(reply.getData(), TEST_DATA);
    }

    @Test
    public void roundtripDifferentCallSignatures() throws Throwable {
        final Event request = new Event();
        request.setType(String.class);
        request.setData(TEST_DATA);
        final Event reply = this.remote.call(METHOD_NAME, request);
        assertEquals(reply.getData(), TEST_DATA);
        assertEquals(TEST_DATA, this.remote.call(METHOD_NAME, TEST_DATA));
    }

    @Test
    public void roundtripEventFutureSyntax() throws Throwable {
        final Future<Event> reply = this.remote.callAsync(METHOD_NAME,
                new Event(String.class, TEST_DATA));
        assertEquals(reply.get().getData(), TEST_DATA);
    }

    @Test
    public void roundtripDataFutureSyntax() throws Throwable {
        final Future<String> reply = this.remote.callAsync(METHOD_NAME,
                TEST_DATA);
        assertEquals(reply.get(), TEST_DATA);
    }

    @Test
    public void roundtripVoidVoid() throws Throwable {
        final Event reply = this.remote.call(VOID_VOID_METHOD_NAME);
        assertEquals(Void.class, reply.getType());
        assertEquals(null, reply.getData());
    }

    @Test
    public void roundtripVoidVoidAsync() throws Throwable {
        final Future<Event> reply = this.remote
                .callAsync(VOID_VOID_METHOD_NAME);
        assertEquals(Void.class, reply.get().getType());
        assertEquals(null, reply.get().getData());
    }

}
