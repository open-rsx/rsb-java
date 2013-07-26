package rsb.transport.socket;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * A reference counting decorator for {@link Bus} instances. The wrapped
 * instance is activated only to the first call to {@link #activate()} and
 * deactivated only when as many {@link #deactivate()} calls are received as
 * have been activate calls received.
 *
 * @author jwienke
 */
public class RefCountingBus implements Bus {

    private final Bus bus;
    private int count = 0;

    public RefCountingBus(final Bus bus) {
        this.bus = bus;
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this.bus) {
            if (this.count == 0) {
                this.bus.activate();
            }
            ++this.count;
        }
    }

    @Override
    public boolean isActive() {
        return this.bus.isActive();
    }

    @Override
    public void deactivate() throws RSBException {
        synchronized (this.bus) {
            if (this.count == 0) {
                throw new IllegalStateException(
                        "Received more deactivation calls than activation calls.");
            }
            --this.count;
            if (this.count == 0) {
                this.bus.deactivate();
            }
        }
    }

    @Override
    public SocketOptions getSocketOptions() {
        return this.bus.getSocketOptions();
    }

    @Override
    public void handleOutgoing(final Notification notification)
            throws RSBException {
        this.bus.handleOutgoing(notification);
    }

    @Override
    public void addNotificationReceiver(final NotificationReceiver receiver) {
        this.bus.addNotificationReceiver(receiver);
    }

}
