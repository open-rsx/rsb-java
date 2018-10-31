/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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
package rsb.transport.socket;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.RsbTestCase;
import rsb.protocol.NotificationType.Notification;

/**
 * Checks that an existing interrupt flag in a sending thread (which might be
 * there from an RSB client), does not degrade the socket transport
 * connectivity.
 *
 * @see <a href="https://code.cor-lab.org/issues/2670">issue 2670</a>
 *
 * @author jwienke
 */
public class SendInterruptTest extends RsbTestCase {

    private BusServer server;
    private BusClient client;
    private ResultWaiter serverResult;
    private ResultWaiter clientResult;

    @Before
    public void setUp() throws Exception {
        this.server = new BusServer(Utilities.getSocketOptions());
        this.server.activate();
        this.serverResult = new ResultWaiter();
        this.server.addNotificationReceiver(this.serverResult);
        this.client = new BusClient(Utilities.getSocketOptions());
        this.client.activate();
        this.clientResult = new ResultWaiter();
        this.client.addNotificationReceiver(this.clientResult);
    }

    @After
    public void tearDown() throws Exception {
        try {
            this.client.deactivate();
        } finally {
            this.server.deactivate();
        }
    }

    /**
     * Sends a notification from the server and the client and for each, checks
     * that they have been received on both sides to validate the connectivity.
     *
     * @throws Exception anything
     */
    private void roundtrip() throws Exception {
        this.server.handleOutgoing(Utilities.createNotification());
        this.clientResult.waitForResult();
        this.serverResult.waitForResult();
        this.client.handleOutgoing(Utilities.createNotification());
        this.clientResult.waitForResult();
        this.serverResult.waitForResult();
    }

    @Test(timeout = 10000)
    public void interruptClientTest() throws Exception {
        final Notification sent = Utilities.createNotification();
        Thread.currentThread().interrupt();
        this.client.handleOutgoing(sent);
        assertTrue(Thread.interrupted());
        roundtrip();
    }

    @Test(timeout = 10000)
    public void interruptServerTest() throws Exception {
        final Notification sent = Utilities.createNotification();
        Thread.currentThread().interrupt();
        this.server.handleOutgoing(sent);
        assertTrue(Thread.interrupted());
        roundtrip();
    }

}
