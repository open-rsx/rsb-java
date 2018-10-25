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
    private static final String VOID_VOID_METHOD_NAME = "voidvoid";

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
    public void serverCallIsInterruptible() throws Exception {
        final Scope scope = new Scope("/a/scope");
        final LocalServer server = Factory.getInstance().createLocalServer(
                scope, Utilities.createParticipantConfig());
        final RemoteServer remote = Factory.getInstance().createRemoteServer(
                scope, Utilities.createParticipantConfig());

        final String method = "longrunningmethod";
        server.addMethod(method, new Callback() {
            @Override
            public Event internalInvoke(
                    final Event request) throws UserCodeException,
                                                InterruptedException {
                // CHECKSTYLE.OFF: MagicNumber
                // Wait longer than the timeout of the test method. If
                // interruption doesn't work, this will end up in the timeout.
                Thread.sleep(40000);
                // CHECKSTYLE.ON: MagicNumber
                return request;
            }
        });

        server.activate();
        try {
            remote.activate();

            try {

                remote.callAsync("mymethod");
                // CHECKSTYLE.OFF: MagicNumber
                Thread.sleep(1000);
                // CHECKSTYLE.ON

            } finally {
                remote.deactivate();
            }

        } finally {
            server.deactivate();
        }
    }

}
