/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.patterns;

import static org.junit.Assert.assertEquals;

// CHECKSTYLE.OFF: UnusedImport
import java.util.concurrent.ExecutionException;
// CHECKSTYLE.ON: UnusedImport
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.RsbTestCase;
import rsb.Scope;
import rsb.Utilities;

/**
 * @author jwienke
 */
public class RpcIntegrationTest extends RsbTestCase {

    private static final int NUM_CALLS = 100;
    private static final String TEST_DATA = "A test String";
    private static final String METHOD_NAME = "myMethod";
    private static final String LONG_RUNNING_METHOD_NAME = "longrunningmethod";
    private static final String VOID_VOID_METHOD_NAME = "voidvoid";
    private static final Scope SERVER_SCOPE = new Scope("/test/server");

    private LocalServer server;
    private RemoteServer remote;

    @Before
    public void setUp() throws Throwable {

        final Scope scope = new Scope("/example/server");
        this.server = Factory.getInstance().createLocalServer(scope,
                Utilities.createParticipantConfig());
        this.server.addMethod(METHOD_NAME, new ReplyDataCallback());
        this.server.addMethod(VOID_VOID_METHOD_NAME,
                new DataCallback<Void, Void>() {

                    @Override
                    public Void invoke(final Void request) throws Exception {
                        return null;
                    }

                });
        this.server.activate();
        this.remote = Factory.getInstance().createRemoteServer(scope,
                Utilities.createParticipantConfig());
        this.remote.activate();

    }

    @After
    public void tearDown() throws Throwable {
        if (this.server != null) {
            this.server.deactivate();
        }
        if (this.remote != null) {
            this.remote.deactivate();
        }
    }

    @Test(timeout = 20000)
    public void roundtripSmoke() throws Throwable {
        for (int i = 0; i < NUM_CALLS; i++) {
            final String reply = this.remote.call(METHOD_NAME, TEST_DATA);
            assertEquals(reply, TEST_DATA);
        }
    }

    @Test(timeout = 20000)
    public void roundtripEventSyntax() throws Throwable {
        final Event reply = this.remote.call(METHOD_NAME, new Event(
                String.class, TEST_DATA));
        assertEquals(reply.getData(), TEST_DATA);
    }

    @Test(timeout = 20000)
    public void roundtripDifferentCallSignatures() throws Throwable {
        final Event request = new Event();
        request.setType(String.class);
        request.setData(TEST_DATA);
        final Event reply = this.remote.call(METHOD_NAME, request);
        assertEquals(reply.getData(), TEST_DATA);
        assertEquals(TEST_DATA, this.remote.call(METHOD_NAME, TEST_DATA));
    }

    @Test(timeout = 20000)
    public void roundtripEventFutureSyntax() throws Throwable {
        final Future<Event> reply = this.remote.callAsync(METHOD_NAME,
                new Event(String.class, TEST_DATA));
        assertEquals(reply.get().getData(), TEST_DATA);
    }

    @Test(timeout = 20000)
    public void roundtripDataFutureSyntax() throws Throwable {
        final Future<String> reply = this.remote.callAsync(METHOD_NAME,
                TEST_DATA);
        assertEquals(reply.get(), TEST_DATA);
    }

    @Test(timeout = 20000)
    public void roundtripVoidVoid() throws Throwable {
        final Event reply = this.remote.call(VOID_VOID_METHOD_NAME);
        assertEquals(Void.class, reply.getType());
        assertEquals(null, reply.getData());
    }

    @Test(timeout = 20000)
    public void roundtripVoidVoidAsync() throws Throwable {
        final Future<Event> reply = this.remote
                .callAsync(VOID_VOID_METHOD_NAME);
        assertEquals(Void.class, reply.get().getType());
        assertEquals(null, reply.get().getData());
    }

    @Test(timeout = 20000)
    public void nonReturningCallIsInterruptible() throws Exception {
        final String method = "nonexistingmethod";
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());

        remote.activate();
        try {
            remote.callAsync(method);
        } finally {
            remote.deactivate();
        }
    }

    private class MyException extends RuntimeException {
        public MyException(final Exception cause) {
            super(cause);
        }
    }

    enum OnInterrupt {
        SWALLOW,
        KEEP,
        USER_EXCEPTION
    }

    LocalServer createLocalServerWithLongRunningMethod(
        final OnInterrupt onInterrupt) throws Exception {
        final LocalServer server =  Factory.getInstance().createLocalServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());
        server.addMethod(LONG_RUNNING_METHOD_NAME, new EventCallback() {
            public Event invoke(final Event request) throws Exception {
                try {
                    // CHECKSTYLE.OFF: MagicNumber
                    // Wait longer than the timeout of the test method. If
                    // interruption doesn't work, this will end up in the timeout.
                    Thread.sleep(40000);
                    // CHECKSTYLE.ON: MagicNumber
                } catch (InterruptedException e) {
                    switch (onInterrupt) {
                    case SWALLOW:
                        break;
                    case KEEP:
                        throw e;
                    case USER_EXCEPTION:
                        throw new MyException(e);
                    default: // cannot happen, but checkstyle :(
                    }
                }
                return request;
            }
        });
        return server;
    }

    @Test(timeout = 20000)
    public void longRunningCallIsInterruptibleRemoteFirst() throws Exception {
        final LocalServer server
            = createLocalServerWithLongRunningMethod(OnInterrupt.KEEP);
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());

        server.activate();
        try {
            remote.activate();
            try {
                remote.callAsync(LONG_RUNNING_METHOD_NAME);
            } finally {
                remote.deactivate();
            }
        } finally {
            server.deactivate();
        }
    }

    @Test(timeout = 20000, expected = java.util.concurrent.ExecutionException.class)
    public void longRunningCallIsInterruptibleLocalFirst() throws Exception {
        final LocalServer server
            = createLocalServerWithLongRunningMethod(OnInterrupt.KEEP);
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());

        server.activate();
        remote.activate();
        try {
            java.util.concurrent.Future<Event> result
                = remote.callAsync(LONG_RUNNING_METHOD_NAME);
            server.deactivate();
            result.get();
        } finally {
            remote.deactivate();
        }
    }

    @Test(timeout = 20000)
    public void swallowingServerCallIsInterruptible() throws Exception {
        final LocalServer server
            = createLocalServerWithLongRunningMethod(OnInterrupt.SWALLOW);
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());

        server.activate();
        try {
            remote.activate();
            try {
                remote.callAsync(LONG_RUNNING_METHOD_NAME);
            } finally {
                remote.deactivate();
            }
        } finally {
            server.deactivate();
        }
    }

    @Test(timeout = 20000)
    public void throwingServerCallIsInterruptible() throws Exception {
        final LocalServer server
            = createLocalServerWithLongRunningMethod(OnInterrupt.USER_EXCEPTION);
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                SERVER_SCOPE, Utilities.createParticipantConfig());

        server.activate();
        try {
            remote.activate();
            try {
                remote.callAsync(LONG_RUNNING_METHOD_NAME);
            } finally {
                remote.deactivate();
            }
        } finally {
            server.deactivate();
        }
    }

}
