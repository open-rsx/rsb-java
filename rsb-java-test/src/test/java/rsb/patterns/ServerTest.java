/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import rsb.Factory;
import rsb.RSBException;
import rsb.RsbTestCase;
import rsb.Scope;
import rsb.Utilities;

/**
 * @author swrede
 */
public class ServerTest extends RsbTestCase {

    private static final String CALL_METHOD_NAME = "callme";
    private static final Logger LOG = Logger.getLogger(ServerTest.class
            .getName());

    public class ShutdownCallback extends DataCallback<String, String> {

        private final Server<?> server;

        public ShutdownCallback(final Server<?> server) {
            this.server = server;
        }

        @Override
        public String invoke(final String request) throws Exception {
            this.server.deactivate();
            return "shutdown now";
        }

    }

    @Test
    public void server() {
        final Server<?> server = this.getServer();
        assertNotNull(server);
    }

    private LocalServer getServer() {
        final Factory factory = Factory.getInstance();
        final LocalServer server =
                factory.createLocalServer(new Scope("/example/server"),
                        Utilities.createParticipantConfig());
        return server;
    }

    /**
     * Test method for {@link rsb.patterns.Server#getMethods()}.
     *
     * @throws Throwable
     *             any exception
     */
    @Test
    public void getMethods() throws Throwable {
        final LocalServer server = this.getServer();
        assertEquals(0, server.getMethods().size());
        server.addMethod(CALL_METHOD_NAME, new ReplyDataCallback());
        assertEquals(1, server.getMethods().size());
        assertEquals(CALL_METHOD_NAME, server.getMethods().iterator().next()
                .getName());
    }

    @Test
    public void addMethod() throws Throwable {
        final LocalServer server = this.getServer();
        server.addMethod(CALL_METHOD_NAME, new ReplyDataCallback());
        server.addMethod("callmeEvent", new ReplyEventCallback());
        assertEquals(2, server.getMethods().size());
    }

    /**
     * Test method for {@link rsb.patterns.Server#activate()}.
     *
     * @throws Throwable
     *             any error
     */
    @Test
    public void activate() throws Throwable {
        final LocalServer server = this.getServer();
        assertFalse(server.isActive());
        server.activate();
        assertTrue(server.isActive());
        server.deactivate();
        assertFalse(server.isActive());
    }

    @Test
    public void activateWithPreRegisteredMethod() throws Throwable {
        final LocalServer server = this.getServer();
        server.addMethod(CALL_METHOD_NAME, new ReplyDataCallback());
        server.activate();
        assertTrue(server.isActive());
        server.deactivate();
        assertFalse(server.isActive());
    }

    /**
     * Test method for {@link rsb.patterns.Server#deactivate()}.
     *
     * @throws Throwable
     *             any error
     */
    @Test
    public void deactivate() throws Throwable {
        final LocalServer server = this.getServer();
        final DataCallback<String, String> method = new ReplyDataCallback();
        server.addMethod(CALL_METHOD_NAME, method);
        server.activate();
        assertTrue(server.isActive());
        assertTrue(server.getMethods().iterator().next().isActive());
        server.deactivate();
        assertFalse(server.isActive());
        assertFalse(server.getMethods().iterator().next().isActive());
    }

    @Test
    @SuppressWarnings("JUnitTestsShouldIncludeAssert")
    public void startServer() throws Throwable {
        final LocalServer server = this.getServer();
        final DataCallback<String, String> method = new ReplyDataCallback();
        server.addMethod(CALL_METHOD_NAME, method);
        server.activate();
        server.addMethod("callmetoo", method);
        server.deactivate();
    }

    @Test
    @SuppressWarnings("JUnitTestsShouldIncludeAssert")
    public void blocking() throws Throwable {
        final LocalServer server = this.getServer();
        final DataCallback<String, String> method =
                new ShutdownCallback(server);
        server.addMethod("shutdown", method);
        server.activate();
        final Thread deactivatingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final int waitUntilDeactivate = 500;
                    Thread.sleep(waitUntilDeactivate);
                } catch (final InterruptedException e) {
                    // must not happen
                }
                LOG.info("Shutting down server from callback.");
                try {
                    server.deactivate();
                } catch (final RSBException e) {
                    // we can't do much about this except signaling this to the
                    // outside world with a runtime exception
                    throw new RuntimeException(e);
                } catch (final InterruptedException e) {
                    // we can't do much about this except signaling this to the
                    // outside world with a runtime exception
                    throw new RuntimeException(e);
                }

            }
        });
        LOG.info("Server running, shutting down in 500ms.");
        deactivatingThread.start();
        server.waitForShutdown();
    }

}
