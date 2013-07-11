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
package rsb.transport;

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.converter.ConversionException;
import rsb.eventprocessing.EventReceivingStrategy;
import rsb.eventprocessing.SingleThreadEventReceivingStrategy;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.filter.FilterObservable;

public class Router extends FilterObservable implements EventHandler {

    private final static Logger LOG = Logger.getLogger(Router.class.getName());

    protected InConnector inPort;
    protected OutConnector outPort;
    protected EventReceivingStrategy ep;
    protected PortConfiguration config;

    // protected EventProcessor epo;
    // protected EventQueue eq = new EventQueue();

    public Router(final TransportFactory f, final PortConfiguration pc) {
        this.config = pc;
        // router setup
        switch (this.config) {
        case NONE:
            break;
        case IN:
            this.setupInPorts(f);
            this.setupEventProcessor();
            break;
        case OUT:
            this.setupOutPorts(f);
            break;
        case INOUT:
            this.setupInPorts(f);
            this.setupOutPorts(f);
            break;
        }
        // epi = new EventProcessor("EP In ["+this.toString()+"]",pi);
        // epi.addObserver(pi);
        // epo = new EventProcessor("EP Out ["+this.toString()+"]",eq);
        // epo.addObserver(po);
        // Subscription os = new Subscription();
        // default out port
        // subscribe(os,po);
    }

    private void setupOutPorts(final TransportFactory factory) {
        this.outPort = factory.createOutConnector();
    }

    private void setupInPorts(final TransportFactory factory) {
        this.inPort = factory.createInPushConnector(this);
        this.addObserver(this.inPort);
    }

    public void activate() throws InitializeException {
        // TODO Think about flexible port assignment
        // subscribers don't need out ports, publishers don't need in-ports
        try {
            switch (this.config) {
            case IN:
                this.inPort.activate();
                break;
            case OUT:
                this.outPort.activate();
                break;
            case INOUT:
                this.outPort.activate();
                break;
            case NONE:
                break;
            }
        } catch (final RSBException e) {
            this.stopEventProcessor();
            LOG.severe("exception occured during port initialization for router");
            throw new InitializeException(e);
        }
    }

    /**
     * Setup internal event processing subsystem using RSB configuration
     * options.
     */
    private void setupEventProcessor() {
        this.ep = new SingleThreadEventReceivingStrategy();
    }

    /**
     * Stop event dispatching gracefully.
     */
    private void stopEventProcessor() {
        if (this.ep != null) {
            try {
                this.ep.shutdownAndWait();
            } catch (final InterruptedException e) {
                LOG.log(Level.WARNING,
                        "Error waiting for shutdown of EventProcessor: ", e);
            }
        }
    }

    /**
     * Publish an {@link Event} over the event bus.
     * 
     * @param e
     *            The {@link Event} to be published
     * @throws ConversionException
     *             error serializing the data contained in the event
     */
    public void publishSync(final Event e) throws ConversionException {
        // TODO add config checks as preconditions
        // send event sync?
        LOG.fine("Router publishing new event to port: [EventID:"
                + e.getId().toString() + ",PortType:" + this.outPort.getType()
                + "]");
        this.outPort.push(e);
    }

    public void deactivate() {
        try {
            if (this.inPort != null) {
                this.inPort.deactivate();
            }
            if (this.outPort != null) {
                this.outPort.deactivate();
            }
            this.stopEventProcessor();
        } catch (final RSBException e) {
            LOG.log(Level.WARNING, "Error waiting for shutdown", e);
        }
    }

    public void addFilter(final Filter filter) {
        this.notifyObservers(filter, FilterAction.ADD);
        this.ep.addFilter(filter);
    }

    public void removeFilter(final Filter filter) {
        this.notifyObservers(filter, FilterAction.REMOVE);
        this.ep.removeFilter(filter);
    }

    public void addHandler(final Handler handler, final boolean wait) {
        this.ep.addHandler(handler, wait);
    }

    public void removeHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.ep.removeHandler(handler, wait);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rsb.transport.EventHandler#deliver(rsb.Event)
     */
    @Override
    public void handle(final Event e) {
        // TODO add config checks as preconditions
        this.ep.handle(e);
    }

}
