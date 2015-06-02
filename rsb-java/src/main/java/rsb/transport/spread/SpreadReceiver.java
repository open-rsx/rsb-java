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
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Activatable;
import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.EventHandler;
import rsb.transport.spread.ReceiverTask.MembershipHandler;
import spread.SpreadException;

/**
 * Implements a spread connection which is used to receive data via a single
 * receiving thread.
 *
 * The internal implementation uses the state pattern.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SpreadReceiver implements Activatable {

    private static final Logger LOG = Logger.getLogger(SpreadReceiver.class
            .getName());

    private final SpreadWrapper spread;
    private final ConverterSelectionStrategy<ByteBuffer> converters;
    private ReceiverTask receiver;
    private final Set<EventHandler> eventHandlers = new HashSet<EventHandler>();

    private State state = new StateInactive();

    /**
     * Utility implementation of {@link MembershipHandler} to wait for a
     * specific membership message indicating an membership change for a group
     * and private group of a connection.
     *
     * Implementations should dispatch the actually interesting membership
     * action to {@link #interestingAction(String, String)} to trigger this
     * class' behavior. The waiting will stop as soon as the first call to
     * {@link #interestingAction(String, String)} is provided with matching
     * group and private group name.
     *
     * @author jwienke
     */
    private abstract static class OneTimeWaitingMembershipHander implements
            MembershipHandler {

        private final Object lock = new Object();

        /**
         * The desired group to join.
         */
        private final String desiredGroup;

        /**
         * The desired connection to join a group as identified by the private
         * group. Will be set to <code>null</code> once the required membership
         * message has been received.
         */
        private String desiredPrivateGroup;

        public OneTimeWaitingMembershipHander(final String desiredGroup,
                final String desiredPrivateGroup) {
            this.desiredGroup = desiredGroup;
            this.desiredPrivateGroup = desiredPrivateGroup;
        }

        public void waitForMessage() throws InterruptedException {
            synchronized (this.lock) {
                while (this.desiredPrivateGroup != null) {
                    this.lock.wait();
                }
            }
            LOG.finest("Waiting for joining finished.");
        }

        protected boolean interestingAction(final String group,
                final String memberGroup) {
            LOG.log(Level.FINEST,
                    "Waiting got notified for member {0} in group "
                            + "{1} while waiting for private group {2}",
                    new Object[] { memberGroup, group, this.desiredPrivateGroup });
            synchronized (this.lock) {
                // in case the desired connection (desirdPrivateGroup) joined
                // the requested group (desiredGroup), notify to stop waiting.
                if (group.equals(this.desiredGroup)
                        && memberGroup.equals(this.desiredPrivateGroup)) {
                    this.desiredPrivateGroup = null;
                    this.lock.notifyAll();
                    return true;
                }
                return false;
            }
        }

    }

    /**
     * Waits until a specific membership message for joining a group occurs. Can
     * only be used once.
     *
     * @author jwienke
     */
    private static class WaitForJoinMembershipHandler extends
            OneTimeWaitingMembershipHander {

        public WaitForJoinMembershipHandler(final String desiredGroup,
                final String desiredPrivateGroup) {
            super(desiredGroup, desiredPrivateGroup);
        }

        @Override
        public boolean joined(final String group, final String memberGroup) {
            return interestingAction(group, memberGroup);
        }

        @Override
        public boolean left(final String group, final String memberGroup) {
            // we don't care about leaving members, so don't unregister this
            // handler
            return false;
        }

    }

    /**
     * Waits until a specific membership message for leaving a group occurs. Can
     * only be used once.
     *
     * @author jwienke
     */
    private static class WaitForLeaveMembershipHandler extends
            OneTimeWaitingMembershipHander {

        public WaitForLeaveMembershipHandler(final String desiredGroup,
                final String desiredPrivateGroup) {
            super(desiredGroup, desiredPrivateGroup);
        }

        @Override
        public boolean joined(final String group, final String memberGroup) {
            // we don't care about joins, so don't unregister this handler
            return false;
        }

        @Override
        public boolean left(final String group, final String memberGroup) {
            return interestingAction(group, memberGroup);
        }

    }

    /**
     * Abstract state base class for the state pattern. All methods throw
     * {@link IllegalStateException} instances as the default behavior.
     *
     * @author jwienke
     */
    private abstract class State {

        public State activate() throws RSBException {
            throw new IllegalStateException();
        }

        public State deactivate() throws RSBException, InterruptedException {
            throw new IllegalStateException();
        }

        public abstract boolean isActive();

        public void join(@SuppressWarnings("unused") final String group)
                throws RSBException, InterruptedException {
            throw new IllegalStateException();
        }

        public void leave(@SuppressWarnings("unused") final String group)
                throws RSBException, InterruptedException {
            throw new IllegalStateException();
        }

    }

    /**
     * Inactive state of the receiver.
     *
     * @author jwienke
     */
    private class StateInactive extends State {

        @Override
        public State activate() throws RSBException {
            LOG.fine("Activating");

            // activate spread connection
            // TODO synchronization?
            if (!SpreadReceiver.this.spread.isActive()) {
                SpreadReceiver.this.spread.activate();
            }

            SpreadReceiver.this.receiver =
                    new ReceiverTask(SpreadReceiver.this.spread,
                            new EventHandler() {

                                @Override
                                public void handle(final Event event) {
                                    SpreadReceiver.this.handle(event);
                                }
                            }, SpreadReceiver.this.converters);
            SpreadReceiver.this.receiver.setPriority(Thread.NORM_PRIORITY + 2);
            SpreadReceiver.this.receiver.setName("ReceiverTask [grp="
                    + SpreadReceiver.this.spread.getPrivateGroup() + "]");
            SpreadReceiver.this.receiver.start();

            return new StateActive();
        }

        @Override
        public boolean isActive() {
            return false;
        }

    }

    /**
     * Active state of the receiver.
     *
     * @author jwienke
     */
    private class StateActive extends State {

        @Override
        public State deactivate() throws RSBException, InterruptedException {
            LOG.fine("Deactivating");
            SpreadReceiver.this.spread.deactivate();
            SpreadReceiver.this.receiver.join();
            return new StateInactive();
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void join(final String group) throws RSBException,
                InterruptedException {
            LOG.log(Level.FINER, "Joining spread group {0}",
                    new Object[] { group });
            try {

                // prepare to wait until the join confirmation has been received
                final WaitForJoinMembershipHandler membershipHandler =
                        new WaitForJoinMembershipHandler(group,
                                SpreadReceiver.this.spread.getPrivateGroup());
                SpreadReceiver.this.receiver
                        .registerMembershipHandler(membershipHandler);

                // join the group
                SpreadReceiver.this.spread.join(group);

                // wait until the membership message confirms the join
                membershipHandler.waitForMessage();

            } catch (final SpreadException e) {
                throw new InitializeException("Unable to join spread group '"
                        + group + "'.", e);
            }
        }

        @Override
        public void leave(final String group) throws RSBException,
                InterruptedException {
            LOG.log(Level.FINER, "Leaving spread group {0}",
                    new Object[] { group });

            // prepare to wait until the join confirmation has been received
            final WaitForLeaveMembershipHandler membershipHandler =
                    new WaitForLeaveMembershipHandler(group,
                            SpreadReceiver.this.spread.getPrivateGroup());
            SpreadReceiver.this.receiver
                    .registerMembershipHandler(membershipHandler);

            // join the group
            SpreadReceiver.this.spread.leave(group);

            // wait until the membership message confirms the join
            membershipHandler.waitForMessage();

        }

    }

    /**
     * Creates a new instance.
     *
     * @param spread
     *            the spread wrapper object to use for the underlying operations
     * @param converters
     *            the converters to user for receiving
     */
    public SpreadReceiver(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        this.spread = spread;
        this.converters = converters;
    }

    @Override
    public void activate() throws RSBException {
        this.state = this.state.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.state = this.state.deactivate();
    }

    @Override
    public boolean isActive() {
        return this.state.isActive();
    }

    /**
     * Adds a handler to be called on every new event received. If the handler
     * is already registered, it will not be added again. Can be called in any
     * state.
     *
     * @param handler
     *            the handler to add. not <code>null</code>
     */
    public void addHandler(final EventHandler handler) {
        synchronized (this.eventHandlers) {
            this.eventHandlers.add(handler);
        }
    }

    /**
     * Removes a handler from set of registered handlers. After return from this
     * method, the respective handler will not be called anymore. Can be called
     * in any state.
     *
     * @param handler
     *            handler to remove
     * @return <code>true</code> if the handler was registered before and
     *         removed successfully now
     */
    public boolean removeHandler(final EventHandler handler) {
        synchronized (this.eventHandlers) {
            return this.eventHandlers.remove(handler);
        }
    }

    /**
     * Join a spread group.
     *
     * @param groupName
     *            name of the spread group, not <code>null</code>, must be <=
     *            {@link SpreadUtilities#MAX_GROUP_NAME_LENGTH}
     * @throws RSBException
     *             unable to join the group
     * @throws InterruptedException
     *             interrupted while waiting for the joining to become active
     */
    public void join(final String groupName) throws RSBException,
            InterruptedException {
        this.state.join(groupName);
    }

    /**
     * Leave a previously joined group.
     *
     * @param groupName
     *            name of the group, not <code>null</code>
     * @throws RSBException
     *             unable to leave the group
     * @throws InterruptedException
     *             interrupted while waiting for the leave to become active
     */
    public void leave(final String groupName) throws RSBException,
            InterruptedException {
        this.state.leave(groupName);
    }

    private void handle(final Event event) {
        synchronized (this.eventHandlers) {
            for (final EventHandler handler : this.eventHandlers) {
                handler.handle(event);
            }
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
