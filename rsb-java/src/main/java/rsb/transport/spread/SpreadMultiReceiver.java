/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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
package rsb.transport.spread;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.EventHandler;

/**
 * A class that encapsulates the logic to correctly handle multiple parallel
 * clients of a {@link SpreadReceiver} by synchronizing the required access and
 * calculating the effective scopes to join. The underlying
 * {@link SpreadReceiver} is activated and deactivated depending on the
 * currently active subscriptions to ensure proper termination in case the last
 * client left.
 *
 * Users of this class call {@link #subscribe(Subscription)} once they want to
 * start receiving events via this class and use
 * {@link #unsubscribe(Subscription)} to stop this by reusing the same
 * {@link Subscription} instance.
 *
 * TODO the whole scope logic in here currently does not calculate the effective
 * root scopes and therefore possibly joins too many scopes. Spread ensures that
 * events are still received only once, but I don't know what the performance
 * impact is.
 *
 * @author jwienke
 */
public class SpreadMultiReceiver {

    private static final Logger LOG = Logger
            .getLogger(SpreadMultiReceiver.class.getName());

    private final SpreadReceiver spread;
    private final Map<Subscription, ScopedHandler> subscriptions =
            new HashMap<Subscription, ScopedHandler>();

    /**
     * An additional list of the available subscriptions sorted by scope to find
     * out how often a scope is being subscribed on so that effective joins and
     * leaves can be calculated.
     */
    private final Map<Scope, Set<Subscription>> scopeSubscriptions =
            new HashMap<Scope, Set<Subscription>>();

    /**
     * Subscription information for a single client of this class.
     *
     * @author jwienke
     */
    public static class Subscription {

        private final Scope scope;
        private final EventHandler handler;

        /**
         * Constructor.
         *
         * @param scope
         *            the scope to subscribe on, not <code>null</code>
         * @param handler
         *            the handler to call in case new events are received on the
         *            provided scope, not <code>null</code>
         */
        public Subscription(final Scope scope, final EventHandler handler) {
            assert scope != null;
            assert handler != null;
            this.scope = scope;
            this.handler = handler;
        }

        /**
         * Returns the scope this subscriptions is interested in.
         *
         * @return a scope, not <code>null</code>
         */
        public Scope getScope() {
            return this.scope;
        }

        /**
         * Returns the handler to call with new events on the scope provide by
         * {@link #getScope()}.
         *
         * @return a handler, not <code>null</code>
         */
        public EventHandler getHandler() {
            return this.handler;
        }

        @Override
        public String toString() {
            return "Subscription[scope=" + this.scope + ", handler="
                    + this.handler + "]";
        }

    }

    /**
     * A handler which ensures that only events on a desire scope or on all of
     * its subscopes are passed through to a wrapped handler instance.
     *
     * @author jwienke
     */
    private class ScopedHandler implements EventHandler {

        private final Scope scope;
        private final EventHandler handler;

        public ScopedHandler(final Scope scope, final EventHandler handler) {
            this.scope = scope;
            this.handler = handler;
        }

        @Override
        public void handle(final Event event) {
            if (event.getScope().equals(this.scope)
                    || event.getScope().isSubScopeOf(this.scope)) {
                this.handler.handle(event);
            }
        }

    }

    /**
     * Constructor.
     *
     * @param spread
     *            the spread receiver to use. Must not be active when passed
     *            into this class, not <code>null</code>
     */
    public SpreadMultiReceiver(final SpreadReceiver spread) {
        assert spread != null;
        assert !spread.isActive();
        this.spread = spread;
    }

    /**
     * Joins the scope required to fulfill this subscription if necessary.
     *
     * @param subscription
     *            new subscription
     * @throws RSBException
     *             error
     * @throws InterruptedException
     *             interrupted while waiting for joining the scope
     */
    private void manageJoin(final Subscription subscription)
            throws RSBException, InterruptedException {
        synchronized (this.subscriptions) {

            if (!this.scopeSubscriptions.containsKey(subscription.getScope())) {
                this.scopeSubscriptions.put(subscription.getScope(),
                        new HashSet<Subscription>());
            }

            // find out whether this is the first subscription on this scope
            final boolean firstScopeSubscription =
                    this.scopeSubscriptions.get(subscription.getScope())
                            .isEmpty();

            // join the group for the subscription scope if this is the first
            // subscription
            if (firstScopeSubscription) {
                LOG.log(Level.FINE,
                        "First subscription for scope {0}: joining spread group {1}",
                        new Object[] {
                                subscription.getScope(),
                                SpreadUtilities.spreadGroupName(subscription
                                        .getScope()) });
                this.spread.join(SpreadUtilities.spreadGroupName(subscription
                        .getScope()));
            }

            // maintain internal state to track scope subscriptions
            final boolean added =
                    this.scopeSubscriptions.get(subscription.getScope()).add(
                            subscription);
            assert added;

        }
    }

    /**
     * Register a new client.
     *
     * @param subscription
     *            the subscription specifying the client needs, not
     *            <code>null</code>
     * @return <code>true</code> if the client was successfully subscribed and
     *         did not exist before
     * @throws RSBException
     *             error
     */
    public boolean subscribe(final Subscription subscription)
            throws RSBException {
        assert subscription != null;
        LOG.log(Level.FINER, "Received new subscription {0}",
                new Object[] { subscription });
        synchronized (this.subscriptions) {
            if (this.subscriptions.containsKey(subscription)) {
                LOG.log(Level.FINER,
                        "Skipping subscription since it already active");
                return false;
            }

            final boolean wasEmpty = this.subscriptions.isEmpty();

            // enable spread if we just got the first receiver
            if (wasEmpty) {
                LOG.log(Level.FINE, "First subscription, activating Spread");
                this.spread.activate();
            }

            // handle spread groups
            try {
                manageJoin(subscription);
            } catch (final InterruptedException e) {
                // restore interruption state
                // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
                Thread.currentThread().interrupt();
                throw new RSBException(e);
            }

            // install the required handler
            final ScopedHandler handler =
                    new ScopedHandler(subscription.getScope(),
                            subscription.getHandler());
            this.spread.addHandler(handler);

            // update internal subscriber list
            this.subscriptions.put(subscription, handler);

            return true;
        }
    }

    /**
     * Leaves the spread group for a scope in case the last subscription for
     * that scope was removed.
     *
     * @param subscription
     *            subscription to remove
     * @throws RSBException
     *             error
     * @throws InterruptedException
     *             interrupted while waiting for leaving the scope
     */
    private void manageLeave(final Subscription subscription)
            throws RSBException, InterruptedException {
        synchronized (this.subscriptions) {

            assert this.scopeSubscriptions.containsKey(subscription.getScope());

            final Set<Subscription> subscriptions =
                    this.scopeSubscriptions.get(subscription.getScope());
            assert subscriptions.contains(subscription);

            // remove the subscription from the internal cache for scope
            // tracking
            final boolean removed = subscriptions.remove(subscription);
            assert removed;

            // if the last subscription for this scope was removed, clean up
            if (subscriptions.isEmpty()) {
                LOG.log(Level.FINE,
                        "Last subscription removed for scope {0}: "
                                + "leaving spread group {1}",
                        new Object[] {
                                subscription.getScope(),
                                SpreadUtilities.spreadGroupName(subscription
                                        .getScope()) });

                // remove the list for the whole scope from the internal data
                // structure to prevent an ever-growing cache
                final Set<Subscription> removedList =
                        this.scopeSubscriptions.remove(subscription.getScope());
                assert removedList != null;
                assert removedList.equals(subscriptions);

                // leave the respective scope
                this.spread.leave(SpreadUtilities.spreadGroupName(subscription
                        .getScope()));
            }

        }
    }

    /**
     * Unregister an existing client.
     *
     * @param subscription
     *            existing subscription to remove, not <code>null</code>
     * @return <code>true</code> if the subscription existed and was removed
     *         successfully
     * @throws RSBException
     *             error
     * @throws InterruptedException
     *             interrupted while waiting for the subscription to become
     *             effectively unregistered
     */
    public boolean unsubscribe(final Subscription subscription)
            throws RSBException, InterruptedException {
        assert subscription != null;
        LOG.log(Level.FINER, "Received unsubscribe request for {0}",
                new Object[] { subscription });
        synchronized (this.subscriptions) {
            if (!this.subscriptions.containsKey(subscription)) {
                LOG.log(Level.WARNING,
                        "Unknown subscription {0}. Skipping unsubscribe request",
                        new Object[] { subscription });
                return false;
            }

            // clean the internal list of subscriptions and retrieve the
            // associated event handler
            final ScopedHandler handler =
                    this.subscriptions.remove(subscription);
            assert handler != null;

            // remove the event handler from the event receiving logic
            final boolean handlerRemoved = this.spread.removeHandler(handler);
            assert handlerRemoved;

            // potentially leave the associated spread group
            manageLeave(subscription);

            // deactivate the spread connection in case the last subscription
            // was removed
            if (this.subscriptions.isEmpty()) {
                LOG.log(Level.FINE,
                        "Last subscription removed, deactivating spread");
                this.spread.deactivate();
            }

            return true;

        }
    }

    /**
     * Returns a URI representing the RSB transport encapsulated by this
     * instance.
     *
     * @return URI describing the transport
     */
    public URI getTransportUri() {
        return this.spread.getTransportUri();
    }

}
