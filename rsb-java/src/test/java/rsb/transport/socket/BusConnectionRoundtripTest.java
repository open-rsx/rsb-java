/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.RsbTestCase;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * @author jwienke
 * @author swrede
 */
public class BusConnectionRoundtripTest extends RsbTestCase {

    private ServerSocket serverSocket;
    private BusClientConnection client;
    private BusServerConnection server;

    // we need to catch all exceptions here to ensure that all instances have a
    // chance to shutdown. Otherwise ports might leak etc.
    @After
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void tearDown() throws Throwable {
        try {
            if (this.client != null) {
                this.client.deactivate();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
        try {
            if (this.server != null) {
                this.server.deactivate();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
    }

    @Before
    public void mutualConnection() throws Throwable {

        this.serverSocket = new ServerSocket(Utilities.getSocketPort());

        this.client = new BusClientConnection(Utilities.getSocketOptions());
        assertFalse(this.client.isActive());

        final Thread activateThread = new Thread() {

            @Override
            public void run() {
                try {
                    BusConnectionRoundtripTest.this.client.activate();
                } catch (final RSBException e) {
                    // nothing to do. is active will notice this
                }
            };

        };
        activateThread.start();

        this.server = new BusServerConnection(this.serverSocket.accept(), true);
        assertFalse(this.server.isActive());

        this.server.activate();

        activateThread.join();

        assertTrue(this.client.isActive());
        assertTrue(this.server.isActive());

    }

    @Test
    public void clientToServer() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.client.sendNotification(sent);

        final Notification received = this.server.readNotification();

        assertEquals(sent, received);

    }

    @Test
    public void serverToClient() throws Throwable {

        final Notification sent = Utilities.createNotification();
        this.server.sendNotification(sent);

        final Notification received = this.client.readNotification();

        assertEquals(sent, received);

    }

}
