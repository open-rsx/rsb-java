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
package rsb.transport.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.RsbTestCase;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * Tests the roundtrip of data through bus server and client.
 *
 * @author jwienke
 */
public class BusRoundtripTest extends RsbTestCase {

    private ResultWaiter serverResult;
    private ResultWaiter clientResult;
    private ResultWaiter secondClientResult;
    private BusServer server;
    private BusClient client;
    private BusClient secondClient;

    @Before
    public void setUp() throws Throwable {

        this.serverResult = new ResultWaiter();
        this.server = new BusServer(Utilities.getSocketOptions());
        this.server.activate();
        this.server.addNotificationReceiver(this.serverResult);

        this.clientResult = new ResultWaiter();
        this.client = new BusClient(Utilities.getSocketOptions());
        this.client.activate();
        this.client.addNotificationReceiver(this.clientResult);

        this.secondClientResult = new ResultWaiter();
        this.secondClient = new BusClient(Utilities.getSocketOptions());
        this.secondClient.activate();
        this.secondClient.addNotificationReceiver(this.secondClientResult);

    }

    @After
    public void tearDown() throws Throwable {

        if (this.secondClient != null) {
            try {
                this.secondClient.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.secondClient = null;
        }
        if (this.client != null) {
            try {
                this.client.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.client = null;
        }
        if (this.server != null) {
            try {
                this.server.deactivate();
            } catch (final RSBException e) {
                // we can't do anything
            }
            this.server = null;
        }

    }

    private void checkResults(final Notification sent) throws Throwable {

        // CHECKSTYLE.OFF: AvoidNestedBlocks - desired here
        final String notReceivedMsg =
                "We must have received something after waiting this long";
        {
            final Notification serverResult = this.serverResult.waitForResult();
            assertNotNull(notReceivedMsg, serverResult);
            assertEquals(sent, serverResult);
        }

        {
            final Notification clientResult = this.clientResult.waitForResult();
            assertNotNull(notReceivedMsg, clientResult);
            assertEquals(sent, clientResult);
        }

        {
            final Notification secondClientResult =
                    this.secondClientResult.waitForResult();
            assertNotNull(notReceivedMsg, secondClientResult);
            assertEquals(sent, secondClientResult);
        }
        // CHECKSTYLE.ON: AvoidNestedBlocks

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromServer() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.server.handleOutgoing(sent);

        checkResults(sent);

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromClient() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.client.handleOutgoing(sent);

        checkResults(sent);

    }

    // asserts are delegated to a separate method
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void fromSecondClient() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.secondClient.handleOutgoing(sent);

        checkResults(sent);

    }

}
