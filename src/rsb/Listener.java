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
package rsb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import rsb.config.TransportConfig;
import rsb.converter.DefaultConverterRepository;
import rsb.eventprocessing.DefaultPushInRouteConfigurator;
import rsb.eventprocessing.PushInRouteConfigurator;
import rsb.filter.Filter;
import rsb.transport.TransportRegistry;

/**
 * This class implements the receiving part of the Inform-Listen (n:m)
 * communication pattern offered by RSB. Upon creation the Listener instance has
 * to be provided with the scope of the channel to listen to. The Listener uses
 * the common event handling mechanisms to process incoming events - in
 * particular filtering of incoming events. Each time a event is received from
 * an Informer, an Event object is dispatched to all Handlers associated to the
 * Listener.
 *
 * @author swrede
 * @author jschaefe
 * @author jwienke
 */
public class Listener extends Participant {

    private static final Logger LOG = Logger
            .getLogger(Listener.class.getName());

    /** The class state currently being active. */
    // false positive due to use in private state classes
    @SuppressWarnings("PMD.ImmutableField")
    private ListenerState state;

    private final List<Filter> filters = new ArrayList<Filter>();
    private final List<Handler> handlers = new ArrayList<Handler>();
    private final PushInRouteConfigurator router;

    /**
     * {@link ListenerState} representing the active state.
     *
     * @author swrede
     */
    protected class ListenerStateActive extends ListenerState {

        /**
         * Constructor.
         *
         * @param listener
         *            the listener to manage
         */
        public ListenerStateActive(final Listener listener) {
            super(listener);
        }

        @Override
        protected void deactivate() throws RSBException, InterruptedException {
            Listener.super.deactivate();
            Listener.this.getRouter().deactivate();
            this.getListener().state = new ListenerStateInactive(
                    this.getListener());
        }

    }

    /**
     * {@link ListenerState} representing the inactive state.
     *
     * @author swrede
     */
    protected class ListenerStateInactive extends ListenerState {

        /**
         * Constructor.
         *
         * @param listener
         *            the listener to manage
         */
        public ListenerStateInactive(final Listener listener) {
            super(listener);
        }

        @Override
        protected void activate() throws RSBException {
            Listener.this.getRouter().activate();
            this.getListener().state = new ListenerStateActive(this.getListener());
            Listener.super.activate();
        }

    }

    /**
     * Creates a listener for a given scope and participant config.
     *
     * @param args arguments used to create this instance.
     * @throws InitializeException
     *             error initializing the listener
     */
    Listener(final ListenerCreateArgs args)
            throws InitializeException {
        super(args);

        this.state = new ListenerStateInactive(this);
        this.router = new DefaultPushInRouteConfigurator(getScope());
        this.router.setEventReceivingStrategy(getConfig()
                .getReceivingStrategy().create());
        for (final TransportConfig transportConfig : getConfig()
                .getEnabledTransports()) {
            this.router.addConnector(TransportRegistry
                    .getDefaultInstance()
                    .getFactory(transportConfig.getName())
                    .createInPushConnector(
                            transportConfig.getOptions(),
                            transportConfig.getConverters(
                                    DefaultConverterRepository
                                            .getDefaultConverterRepository())
                                    .getConvertersForDeserialization()));
        }
        LOG.fine("New Listener instance: [scope=" + this.getScope() + "]");

    }

    /**
     * Returns the router used for this participant.
     *
     * @return router used for this participant, not <code>null</code>
     */
    private PushInRouteConfigurator getRouter() {
        return this.router;
    }

    @Override
    public void activate() throws RSBException {
        this.state.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.state.deactivate();
    }

    /**
     * Returns the filters currently being active on the listener.
     *
     * @return list of active filters
     */
    public List<Filter> getFilters() {
        return this.filters;
    }

    /**
     * Returns an iterator overall active filters on this listener.
     *
     * @return filter iterator
     */
    public Iterator<Filter> getFilterIterator() {
        return this.filters.iterator();
    }

    /**
     * Activates the specified filter for this informer.
     *
     * @param filter
     *            the filter to activate
     */
    public void addFilter(final Filter filter) {
        this.filters.add(filter);
        this.getRouter().filterAdded(filter);
    }

    /**
     * Returns a list of handlers attached to this listener.
     *
     * @return list of handlers
     */
    public List<Handler> getHandlers() {
        return this.handlers;
    }

    /**
     * Returns an interator over all handlers attached to this listener.
     *
     * @return handler interator
     */
    public Iterator<Handler> getHandlerIterator() {
        return this.handlers.iterator();
    }

    /**
     * Register an event handler on this Listener to be notified about incoming
     * events. All received events will be send to the registered listeners.
     *
     * @param handler
     *            the handler instance to be registered
     * @param wait
     *            if set to @c true, this method will return only after the
     *            handler has completely been installed and will receive the
     *            next available message. Otherwise it may return earlier.
     * @throws InterruptedException
     *             if waiting for installation is wanted but interrupted
     */
    public void addHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.handlers.add(handler);
        this.getRouter().handlerAdded(handler, wait);
    }

    /**
     * Remove an event listener from this Listener.
     *
     * @param handler
     *            the listener instance to be removed.
     * @param wait
     *            if set to @c true, this method will return only after the
     *            handler has been completely removed from the event processing
     *            and will not be called anymore from this listener.
     * @throws InterruptedException
     *             thrown if the method is interrupted while waiting for the
     *             handler to be removed
     */
    public void removeHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.handlers.remove(handler);
        this.getRouter().handlerRemoved(handler, wait);
    }

    @Override
    public boolean isActive() {
        return this.state.getClass() == ListenerStateActive.class;
    }

    @Override
    public String getKind() {
        return "listener";
    }

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

}
