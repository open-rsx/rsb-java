/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import rsb.Handler;
import rsb.InitializeException;
import rsb.Event;
import rsb.RSBException;
import rsb.converter.ConversionException;
import rsb.eventprocessing.SingleThreadEventReceivingStrategy;
import rsb.eventprocessing.EventReceivingStrategy;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.filter.FilterObservable;

public class Router extends FilterObservable implements EventHandler {

	private final static Logger LOG = Logger.getLogger(Router.class.getName());

	protected Port inPort;
	protected Port outPort;
	protected EventReceivingStrategy ep;
	protected PortConfiguration config;

	// protected EventProcessor epo;
	// protected EventQueue eq = new EventQueue();

	public Router(TransportFactory f, PortConfiguration pc) {
		config = pc;
		// router setup
		switch (config) {
		case NONE:
			break;		
		case IN:
			setupInPorts(f);
			setupEventProcessor();
			break;
		case OUT:
			setupOutPorts(f);
			break;
		case INOUT:
			setupInPorts(f);
			setupOutPorts(f);
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

	/**
	 * @param f
	 */
	private void setupOutPorts(final TransportFactory factory) {
		outPort = factory.createPort();
	}

	/**
	 * @param f
	 */
	private void setupInPorts(final TransportFactory factory) {
		inPort = factory.createPort(this);
		addObserver(inPort);
	}

	public void activate() throws InitializeException {
		// TODO Think about flexible port assignment
		// subscribers don't need out ports, publishers don't need in-ports
		try {
			switch (config) {
			case IN:
				inPort.activate();
				break;
			case OUT:
				outPort.activate();
				break;
			case INOUT:
				outPort.activate();
				break;
			}
		} catch (RSBException e) {
			stopEventProcessor();
			LOG.severe("exception occured during port initialization for router");
			throw new InitializeException(e);
		}
	}

	/**
	 * Setup internal event processing subsystem using RSB configuration
	 * options.
	 */
	private void setupEventProcessor() {
		ep = new SingleThreadEventReceivingStrategy();
	}
	
	/**
	 * Stop event dispatching gracefully.
	 */
	private void stopEventProcessor() {
		if (ep != null) {
			try {
				ep.shutdownAndWait();
			} catch (InterruptedException e) {
				LOG.log(Level.WARNING, "Error waiting for shutdown of EventProcessor: ", e);
			}
		}		
	}

	/**
	 * Publish an {@link Event} over the event bus.
	 * 
	 * @param e
	 *            The {@link Event} to be published
	 */
//	public void publish(Event e) {
//		// TODO add config checks as preconditions
//		// send event async
//		throw new RuntimeException("Router::publish method not implemented!");
//	}

	public void publishSync(Event e) throws ConversionException {
		// TODO add config checks as preconditions
		// send event sync?
		LOG.fine("Router publishing new event to port: [EventID:"
				+ e.getId().toString() + ",PortType:" + outPort.getType() + "]");
		outPort.push(e);
	}

	public void deactivate() {
		try {
			if (inPort != null)
				inPort.deactivate();
			if (outPort != null)
				outPort.deactivate();
			stopEventProcessor();
		} catch (RSBException e) {
			LOG.log(Level.WARNING, "Error waiting for shutdown", e);
		}
	}

	public void addFilter(Filter filter) {
		notifyObservers(filter, FilterAction.ADD);
		ep.addFilter(filter);
	}

	public void removeFilter(Filter filter) {
		notifyObservers(filter, FilterAction.REMOVE);
		ep.removeFilter(filter);
	}

	public void addHandler(Handler handler, boolean wait) {
		ep.addHandler(handler, wait);
	}

	public void removeHandler(Handler handler, boolean wait)
			throws InterruptedException {
		ep.removeHandler(handler, wait);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsb.transport.EventHandler#deliver(rsb.Event)
	 */
	@Override
	public void handle(Event e) {
		// TODO add config checks as preconditions
		ep.handle(e);
	}

}
