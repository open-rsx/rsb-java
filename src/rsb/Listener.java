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

import rsb.filter.Filter;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

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

    protected final static Logger LOG = Logger.getLogger(Listener.class
            .getName());

    /** class state variable */
    private ListenerState state;

    @SuppressWarnings({ "deprecation", "unused" })
    private ErrorHandler errorHandler;

    private final List<Filter> filters = new ArrayList<Filter>();
    private final List<Handler> handlers = new ArrayList<Handler>();

    protected class ListenerStateActive extends ListenerState {

        public ListenerStateActive(final Listener ctx) {
            super(ctx);
        }

        @Override
        protected void deactivate() {
            Listener.this.getRouter().deactivate();
            this.getContext().state = new ListenerStateInactive(
                    this.getContext());
        }

    }

    protected class ListenerStateInactive extends ListenerState {

        public ListenerStateInactive(final Listener ctx) {
            super(ctx);
        }

        @Override
        protected void activate() throws InitializeException {
            Listener.this.getRouter().activate();
            this.getContext().state = new ListenerStateActive(this.getContext());
        }

    }

    Listener(final Scope scope) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
        this.initMembers();
    }

    Listener(final String scope) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
        this.initMembers();
    }

    Listener(final String scope, final TransportFactory tfac) {
        super(scope, tfac, PortConfiguration.IN);
        this.initMembers();
    }

    Listener(final Scope scope, final TransportFactory tfac) {
        super(scope, tfac, PortConfiguration.IN);
        this.initMembers();
    }

    private void initMembers() {
        this.state = new ListenerStateInactive(this);
        this.errorHandler = new DefaultErrorHandler(LOG);
        LOG.fine("New Listener instance: [scope=" + this.getScope() + "]");
    }

    @Override
    public void activate() throws InitializeException {
        this.state.activate();
    }

    @Override
    public void deactivate() {
        this.state.deactivate();
    }

    public List<Filter> getFilters() {
        return this.filters;
    }

    public Iterator<Filter> getFilterIterator() {
        return this.filters.iterator();
    }

    public void addFilter(final Filter filter) {
        this.filters.add(filter);
        this.getRouter().addFilter(filter);
    }

    public List<Handler> getHandlers() {
        return this.handlers;
    }

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
     */
    public void addHandler(final Handler handler, final boolean wait) {
        this.handlers.add(handler);
        this.getRouter().addHandler(handler, wait);
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
        this.getRouter().removeHandler(handler, wait);
    }

    /**
     * @param handler
     *            an error handler to call on errors
     * @deprecated not yet designed
     */
    @Deprecated
    public void setErrorHandler(final ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override
    public boolean isActive() {
        return this.state.getClass() == ListenerStateActive.class;
    }

}
