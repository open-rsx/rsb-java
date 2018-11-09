/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import rsb.AbstractEventHandler;
import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.Listener;
import rsb.ListenerCreateArgs;
import rsb.Participant;
import rsb.ReaderCreateArgs;
import rsb.RSBException;
import rsb.Activatable;

/**
 * Implements synchronous event receiving as a pattern on top of the usual
 * asynchronous participant types.
 *
 * Clients have to continuously poll for new events by calling the #read()
 * method. Internally, a queue is used to buffer received events.
 *
 * @author jwienke
 */
public class Reader extends Participant {

    private State state;
    private final Listener listener;
    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

    private abstract class State extends Activatable.State {
        // pull into own namespace for nice state class names
        public Event read(final long timeout,
                final TimeUnit unit) throws RSBException, InterruptedException {
            throw new IllegalStateException(
                    "Reading is only possible in active state");
        }
    }

    private class StateInactive extends State {

        @Override
        public void activate() throws RSBException {
            Reader.super.activate();
            Reader.this.listener.activate();
            try {
                Reader.this.listener.addHandler(new AbstractEventHandler() {

                    @Override
                    public void handleEvent(final Event event) {
                        Reader.this.queue.add(event);
                    }

                }, true);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RSBException(e);
            }
            Reader.this.state = new StateActive();
        }

        @Override
        public boolean isActive() {
            return false;
        }

    }

    private class StateActive extends State {

        @Override
        public void deactivate() throws RSBException, InterruptedException {
            Reader.super.deactivate();
            Reader.this.state = new StateTerminal();
            Reader.this.listener.deactivate();
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public Event read(final long timeout,
                final TimeUnit unit) throws RSBException, InterruptedException {
            return Reader.this.queue.poll(timeout, unit);
        }
    }

    private class StateTerminal extends State {

        @Override
        public boolean isActive() {
            return false;
        }

    }

    /**
     * Creates a new reader instance.
     *
     * @param args
     *            Arguments for creating the participant
     * @param factory
     *            Factory instance used for creating the internal listener
     * @throws InitializeException
     *             error initializing the reader
     */
    public Reader(final ReaderCreateArgs args,
            final Factory factory) throws InitializeException {
        super(args);

        this.state = new StateInactive();

        final ListenerCreateArgs listenerArgs = new ListenerCreateArgs();
        listenerArgs.setScope(args.getScope());
        listenerArgs.setConfig(args.getConfig());
        listenerArgs.setParent(this);
        this.listener = factory.createListener(listenerArgs);
    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.state.isActive();
        }
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            this.state.activate();
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            this.state.deactivate();
        }
    }

    @Override
    public Set<URI> getTransportUris() {
        return new HashSet<URI>();
    }

    /**
     * Read the next event without blocking.
     *
     * If available, returns the next event that was received.
     *
     * @return an event of <code>null</code> if no unread event was received
     *         yet.
     * @throws RSBException
     *             reading the event failed
     * @throws InterruptedException
     *             interrupted while reading
     */
    public Event read() throws RSBException, InterruptedException {
        return this.read(0, TimeUnit.SECONDS);
    }

    /**
     * Read the next event and block until one is available.
     *
     * Waits up to the specified amount of time for receiving the next event.
     *
     * @param timeout
     *            the amount of time to wait
     * @param unit
     *            the unit of the amount of time to wait
     * @return an event of <code>null</code> if no unread event was received
     *         until timeout.
     * @throws RSBException
     *             reading the event failed
     * @throws InterruptedException
     *             interrupted while reading
     */
    public Event read(
            final long timeout, final TimeUnit unit) throws RSBException,
           InterruptedException {
        synchronized (this) {
            return this.state.read(timeout, unit);
        }
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

    @Override
    public String getKind() {
        return "reader";
    }

}
