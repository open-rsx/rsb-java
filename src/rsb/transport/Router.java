/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.transport;

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.RSBException;
import rsb.event.EventProcessor;
import rsb.event.Subscription;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.filter.FilterObservable;
import rsb.util.Properties;

public class Router extends FilterObservable implements EventHandler {

	private final static Logger log = Logger.getLogger(Router.class.getName());

	protected Port inPort;
	protected Port outPort;
	protected EventProcessor ep;
	protected PortConfiguration config;

	// protected EventProcessor epo;
	// protected EventQueue eq = new EventQueue();

	public Router(TransportFactory f, PortConfiguration pc) {
		config = pc;
		// router setup
		switch (config) {
		case IN:
			setupInPorts(f);
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
	private void setupOutPorts(TransportFactory f) {
		outPort = f.createPort();
	}

	/**
	 * @param f
	 */
	protected void setupInPorts(TransportFactory f) {
		inPort = f.createPort(this);
		addObserver(inPort);
	}

	public void activate() throws InitializeException {
		// TODO Think about flexible port assignment
		// subscribers don't need out ports, publishers don't need in-ports
		try {
			switch (config) {
			case IN:
				inPort.activate();
				setupEventProcessor();
				break;
			case OUT:
				outPort.activate();
				break;
			case INOUT:
				outPort.activate();
				break;
			}
		} catch (RSBException e) {
			log.severe("exception occured during port initialization for router");
			throw new InitializeException(e);
		}
	}

	/**
	 * Setup internal event processing subsystem using RSB configuration
	 * options.
	 */
	protected void setupEventProcessor() {
		// extract parameters for ExecutorService from configuration
		int cSize = Properties.getInstance().getPropertyAsInt(
				"RSB.ThreadPool.Size");
		int mSize = Properties.getInstance().getPropertyAsInt(
				"RSB.ThreadPool.SizeMax");
		int qSize = Properties.getInstance().getPropertyAsInt(
				"RSB.ThreadPool.QueueSize");

		ep = new EventProcessor(cSize, mSize, qSize);
	}

	/**
	 * Publish an RSBEvent over the event bus.
	 * 
	 * @param e
	 *            The RSBEvent to be published
	 */
	public void publish(RSBEvent e) {
		// TODO add config checks as preconditions
		// send event async
		throw new RuntimeException("Router::publish method not implemented!");
	}

	public void publishSync(RSBEvent e) {
		// TODO add config checks as preconditions
		// send event sync?
		log.fine("Router publishing new event to port: [EventID:"
				+ e.getId().toString() + ",PortType:" + outPort.getType() + "]");
		outPort.push(e);
	}

	public void deactivate() {
		try {
			if (inPort != null)
				inPort.deactivate();
			if (outPort != null)
				outPort.deactivate();
			if (ep != null) {
				ep.waitForShutdown();
			}
		} catch (RSBException e) {
			log.log(Level.WARNING, "Error waiting for shutdown", e);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "Error waiting for shutdown", e);
		}
	}

	/**
	 * Subscribes a listener to all events that pass it's filter chain.
	 */
	public void subscribe(Subscription sub) {
		// TODO add config checks as preconditions
		for (Filter f : sub.getFilter()) {
			notifyObservers(f, FilterAction.ADD);
		}
		ep.addSubscription(sub);
	}

	// public void unsubscribe(Subscription sub, XcfEventListener sink) {
	// epi.remove(sub, sink);
	// }
	//
	public void unsubscribe(Subscription sub) {
		// TODO add config checks as preconditions
		ep.removeSubscription(sub);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsb.transport.EventHandler#deliver(rsb.RSBEvent)
	 */
	@Override
	public void handle(RSBEvent e) {
		// TODO add config checks as preconditions
		ep.fire(e);
	}

}
