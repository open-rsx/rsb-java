package rsb.transport.socket;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Test;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * @author jwienke
 */
public class BusCacheTest {

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
        public void handleIncoming(final Notification notification)
                throws RSBException {
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
    public void getNotCached() throws Throwable {
        final BusCache cache = new BusCache();
        assertNull(cache.get(new SocketOptions(InetAddress.getLocalHost(),
                2546, true)));
    }

}
