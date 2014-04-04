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
    private final DeactivationHandler deactivationHandler;

    /**
     * A handler that will be called once the underlying bus will really be
     * deactivated.
     *
     * @author jwienke
     */
    public interface DeactivationHandler {

        /**
         * Called on deactivation of a reference-counted bus instance.
         *
         * @param bus
         *            the bus being deactivated
         */
        void deactivated(final RefCountingBus bus);

    }

    /**
     * Constructor.
     *
     * @param bus
     *            bus to manage
     * @param handler
     *            handler to be called on deactivation of the bus
     */
    public RefCountingBus(final Bus bus, final DeactivationHandler handler) {
        this.bus = bus;
        this.deactivationHandler = handler;
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
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this.bus) {
            if (this.count == 0) {
                throw new IllegalStateException(
                        "Received more deactivation calls than activation calls.");
            }
            --this.count;
            if (this.count == 0) {
                this.bus.deactivate();
                this.deactivationHandler.deactivated(this);
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

    @Override
    public void removeNotificationReceiver(final NotificationReceiver receiver) {
        this.bus.removeNotificationReceiver(receiver);
    }

    /**
     * Returns the underlying bus that is handled with reference counting. Do
     * not call {@link Bus#activate()} or {@link Bus#deactivate()} on this
     * instance!
     *
     * @return the managed bus
     */
    public Bus getContainedBus() {
        return this.bus;
    }

}
