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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Test;

import rsb.LoggingEnabled;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * @author jwienke
 */
public class BusCacheTest extends LoggingEnabled {

    class DummyBus extends BusBase {

        public DummyBus(final SocketOptions options) {
            super(options);
        }

        @Override
        public void activate() throws RSBException {
            // dummy method
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void handleIncoming(final Notification notification,
                final BusConnection source) throws RSBException {
            // dummy method
        }

    }

    @Test
    public void registerAndGet() throws Throwable {

        final BusCache cache = new BusCache();
        final SocketOptions options = new SocketOptions(
                InetAddress.getLocalHost(), 2546, true);
        final Bus bus = new DummyBus(options);
        cache.register(bus);
        assertSame(bus, cache.get(options));

    }

    @Test(expected = IllegalArgumentException.class)
    public void registerDuplicateError() throws Throwable {

        final BusCache cache = new BusCache();
        final SocketOptions options = new SocketOptions(
                InetAddress.getLocalHost(), 2546, true);
        cache.register(new DummyBus(options));
        cache.register(new DummyBus(options));

    }

    @Test
    public void registerReplace() throws Throwable {

        final BusCache cache = new BusCache();
        final SocketOptions options = new SocketOptions(
                InetAddress.getLocalHost(), 2546, true);
        final DummyBus second = new DummyBus(options);
        cache.register(new DummyBus(options));
        cache.register(second, true);
        assertSame(second, cache.get(options));

    }

    @Test
    public void hasBus() throws Throwable {

        final BusCache cache = new BusCache();
        final SocketOptions options = new SocketOptions(
                InetAddress.getLocalHost(), 2546, true);
        final Bus bus = new DummyBus(options);
        cache.register(bus);
        assertTrue(cache.hasBus(options));

    }

    @Test
    public void unregister() throws Throwable {

        final BusCache cache = new BusCache();
        final SocketOptions options = new SocketOptions(
                InetAddress.getLocalHost(), 2546, true);
        final Bus bus = new DummyBus(options);
        cache.register(bus);
        cache.unregister(bus);
        assertFalse(cache.hasBus(options));
        cache.unregister(bus);

    }

    @Test
    public void getNotCached() throws Throwable {
        final BusCache cache = new BusCache();
        final int randomPort = 2546;
        assertNull(cache.get(new SocketOptions(InetAddress.getLocalHost(),
                randomPort, true)));
    }

}
