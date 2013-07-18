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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Ignore;
import org.junit.Test;

import rsb.protocol.NotificationType.Notification;

/**
 * @author swrede
 */
public class BusTest {

    @Test
    @Ignore
    public void handleOutgoing() throws Throwable {
        final Bus bus = new BusClient(InetAddress.getLocalHost(), 55555);
        final BusClientConnection con = new BusClientConnection(
                InetAddress.getLocalHost(), 55555);
        con.activate();
        bus.addConnection(con);
        bus.addConnection(con);
        assertEquals(2, bus.numberOfConnections());
        final Notification n = Notification.getDefaultInstance();
        bus.handleOutgoing(n);
        bus.removeConnection(con);
        assertEquals(1, bus.numberOfConnections());
    }

    @Test
    public void addandRemoveConnection() throws UnknownHostException {
        final Bus bus = new BusClient(InetAddress.getLocalHost(), 55555);
        assertEquals(0, bus.numberOfConnections());
        final BusClientConnection con = new BusClientConnection(
                InetAddress.getLocalHost(), 55555);
        bus.addConnection(con);
        assertEquals(1, bus.numberOfConnections());
        bus.removeConnection(con);
        assertEquals(0, bus.numberOfConnections());
    }

}
