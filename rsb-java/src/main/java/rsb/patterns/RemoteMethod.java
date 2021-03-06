/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;
import rsb.Handler;
import rsb.InformerCreateArgs;
import rsb.InitializeException;
import rsb.ListenerCreateArgs;
import rsb.ParticipantCreateArgs;
import rsb.RSBException;
import rsb.filter.MethodFilter;

/**
 * Objects of this class represent methods provided by a remote server.
 *
 * @author jmoringe
 * @author swrede
 * @author jwienke
 */
public class RemoteMethod extends Method implements Handler {

    private static final Logger LOG = Logger.getLogger(RemoteMethod.class
            .getName());

    // assuming usually eight threads will write simultaneously to the map
    private final Map<EventId, FuturePreparator<?>> pendingRequests =
            new ConcurrentHashMap<EventId, FuturePreparator<?>>(16, 0.75f, 8);

    /**
     * A specialized active state that cancels pending requests on
     * exit.
     */
    protected class StateActive extends Method.StateActive {

        @Override
        public void deactivate() throws RSBException, InterruptedException {
            LOG.finer("Canceling all in-progress requests");
            synchronized (RemoteMethod.this) {
                for (final FuturePreparator<?> request
                         : RemoteMethod.this.pendingRequests.values()) {
                    request.error(new RSBException("Server deactivated"));
                }
                RemoteMethod.this.pendingRequests.clear();
            }
            super.deactivate();
        }

    }

    /**
     * Instances of this class are used to prepare a {@link Future} instance
     * containing the desired result of the client from the reply {@link Event}
     * instance.
     *
     * @author jwienke
     * @param <FutureDataType>
     *            the data type of the contents inside the result future
     */
    public abstract static class FuturePreparator<FutureDataType> {

        private final WeakReference<Future<FutureDataType>> future;

        /**
         * Creates a new instance with a {@link Future} instance that is
         * eventually passed to the client. Internally a {@link WeakReference}
         * will be used to track this future. This allows the client to ignore
         * the result future in a way that we sometimes can notice this and
         * avoid additional work.
         *
         * @param future
         *            the future to contain the final result.
         */
        public FuturePreparator(final Future<FutureDataType> future) {
            assert future != null;
            this.future = new WeakReference<Future<FutureDataType>>(future);
        }

        /**
         * Returns the future where the result should be prepared for.
         *
         * @return {@link Future} instance or <code>null</code> if the client
         *         does not hold a reference on this future anymore. In such a
         *         case nothing needs to be done.
         */
        public Future<FutureDataType> getFuture() {
            return this.future.get();
        }

        /**
         * This method needs to be implemented with the conversion logic from
         * the {@link Event} instance with the result to the final
         * {@link Future} interface.
         *
         * @param resultEvent
         *            result event from the server call
         */
        public abstract void result(final Event resultEvent);

        /**
         * Method called in case of an error. The default implementation
         * directly passes the given {@link Throwable} to the the
         * {@link Future#error(Throwable)} method.
         *
         * @param error
         *            exception explaining the error
         */
        public void error(final Throwable error) {
            final Future<FutureDataType> future = this.future.get();
            if (future != null) {
                future.error(error);
            }
        }

    }

    /**
     * Create a new RemoteMethod object that represent the remote method named @a
     * name provided by @a server.
     *
     * @param args the arguments used to create this method
     * @throws InterruptedException
     *             error while installing method
     * @throws InitializeException
     *             error initializing the method or one of the underlying
     *             participants
     */
    public RemoteMethod(final ParticipantCreateArgs<?> args)
        throws InterruptedException, InitializeException {
        super(args);
        this.setListener(getFactory().createListener(
                new ListenerCreateArgs().setScope(this.getScope())
                        .setConfig(this.getConfig()).setParent(this)));
        this.setInformer(getFactory().createInformer(
                new InformerCreateArgs().setScope(this.getScope())
                        .setConfig(this.getConfig()).setParent(this)));
        this.getListener().addFilter(new MethodFilter("REPLY"));
        this.getListener().addHandler(this, true);
    }

    @Override
    protected State createActiveState() {
        return new StateActive();
    }

    /**
     * @param request
     *            the request from the caller
     * @param resultPreparator
     *            strategy for preparing the result {@link Future} instance from
     *            the received reply event
     * @throws RSBException
     *             in case of communication errors
     */
    void call(final Event request, final FuturePreparator<?> resultPreparator)
            throws RSBException {
        // set metadata
        request.setScope(this.getScope());
        request.setMethod("REQUEST");
        // further metadata is set by informer

        synchronized (this) {
            final Event sentEvent = this.getInformer().publish(request);
            // put future with id as weak ref in pending results table
            this.pendingRequests.put(sentEvent.getId(), resultPreparator);
            LOG.fine("registered future in pending requests with id: "
                    + sentEvent.getId());
        }
    }

    @Override
    public void internalNotify(final Event event) {

        if (event.getCauses().size() <= 0) {
            LOG.warning("Received reply event without cause. Skipping it.");
            return;
        }

        final EventId replyId = event.getCauses().iterator().next();
        LOG.fine("Received reply with id: " + replyId);

        // check for reply id in list of pending calls
        FuturePreparator<?> request = null;
        synchronized (this) {
            if (this.pendingRequests.containsKey(replyId)) {
                request = this.pendingRequests.get(replyId);
                this.pendingRequests.remove(replyId);
            } else {
                LOG.finer("Received a reply for a different RemoteServer instance.");
                return;
            }
        }

        // If several clients send requests to the same server
        // method, and EVENT is the reply to one of the
        // "other" requests, REQUEST can be null here.
        LOG.fine("Found pending reply for id: " + replyId);
        // an error occurred at the server side
        if (event.getMetaData().hasUserInfo("rsb:error?")) {
            final String error = (String) event.getData();
            request.error(new RSBException(error));
            return;
        }
        request.result(event);

    }

    @Override
    public String getKind() {
        return "remote-method";
    }

    @Override
    public Class<?> getDataType() {
        return null;
    }

    @Override
    public Set<URI> getTransportUris() {
        return new HashSet<URI>();
    }

};
