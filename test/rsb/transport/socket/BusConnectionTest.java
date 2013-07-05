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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import rsb.Event;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;

/**
 * @author swrede
 * 
 */
public class BusConnectionTest {

    @Test
    public void busConnection() throws UnknownHostException {
        final InetAddress addr = InetAddress.getLocalHost();
        final BusConnection bus1 = new BusConnection(addr, 55555, false);
        assertNotNull(bus1);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // @Test
    // TODO add Server mock for testing
    public void busConnectionDeActivation() throws IOException,
            RSBException {
        // instantiate Socket object
        // precondition: server is running!!!
        final Socket socket1 = new Socket(InetAddress.getLocalHost(), 55555);
        assertNotNull(socket1);
        final Socket socket2 = new Socket(InetAddress.getLocalHost(), 55555);
        assertNotNull(socket2);
        final InetAddress addr = InetAddress.getLocalHost();
        final BusConnection bus1 = new BusConnection(addr, 55555);
        bus1.activate();
        bus1.deactivate();
        bus1.activate();
        bus1.deactivate();
        bus1.activate();
    }

    @Test
    public void clientConnection() throws RSBException {
        try {
            // prototyping code that works with rsb_listener example
            final InetAddress addr = InetAddress.getLocalHost();
            final BusConnection bus = new BusConnection(addr, 55555);
            bus.activate();

            // process packet
            int i = 0;
            while (true) {
                i++;
                System.out.println("Waiting for Notification #" + i);
                final Notification n = bus.readNotification();
                // convert to Event
                final Event e = EventBuilder.fromNotification(n);

                System.out.println("Scope: " + e.getScope());
                System.out.println("Id: " + e.getId());
                System.out.println("------------------------------");
                if (i == 1200) {
                    break;
                }
            }

        } catch (final UnknownHostException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            // cleanup
        }

    }

    // private void saveRawNotification(ByteBuffer buf_notificaton) {
    // System.out.println("Extra bytes read: " + rbc.read(buf_notification));

    // File file = new File("pbuf.data");
    //
    // // Set to true if the bytes should be appended to the file;
    // // set to false if the bytes should replace current bytes
    // // (if the file exists)
    // boolean append = false;
    //
    // try {
    // // Create a writable file channel
    // FileChannel wChannel = new FileOutputStream(file, append).getChannel();
    //
    // // Write the ByteBuffer contents; the bytes between the ByteBuffer's
    // // position and the limit is written to the file
    // wChannel.write(buf_notification);
    //
    // // Close the file
    // wChannel.close();
    // } catch (IOException e) {
    // }

    @Test
    public void serverConnection() {
        try {
            // prototyping code that works with rsb_listener example
            final InetAddress addr = InetAddress.getLocalHost();
            final BusConnection bus = new BusConnection(addr, 55555, true);
            bus.activate();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
