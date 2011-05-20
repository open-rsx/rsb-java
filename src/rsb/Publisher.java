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
package rsb;

import java.util.logging.Logger;

import rsb.transport.PortConfiguration;
import rsb.transport.Router;
import rsb.transport.TransportFactory;

/**
 * This class offers a method to send messages to any registered Subscribers.
 * 1:m-communication (called Publish-Subscribe) is one of the basic
 * communication patterns offered by RSB. Each Publisher instance must be given
 * a unique name which is used by Subscriber instances to connect to the
 * Publisher. The Publisher also offers a method to retrieve all Subscribers
 * currently registered at the instance.
 * 
 * @author swrede
 * @author rgaertne
 * @author jschaefe
 */
public class Publisher<T> implements RSBObject {

	private final static Logger log = Logger.getLogger(Publisher.class
			.getName());

	/** publisher's name within it's scope */
	protected Scope scope;

	/** state variable for publisher instance */
	protected PublisherState<T> state;

	/** transport factory object */
	protected TransportFactory transportFactory;

	/** default data type for this publisher */
	protected String typeinfo;

	/** transport router */
	protected Router router;

	protected class PublisherStateInactive extends PublisherState<T> {

		protected PublisherStateInactive(Publisher<T> ctx) {
			super(ctx);
			log.fine("Publisher state activated: [Scope:" + scope
					+ ",State:Inactive,Type:" + typeinfo + "]");
		}

		protected void activate() throws InitializeException {
			router.activate();
			p.state = new PublisherStateActive(p);
			log.info("Publisher activated: [Scope:" + scope + ",Type:"
					+ typeinfo + "]");
		}

	}

	protected class PublisherStateActive extends PublisherState<T> {

		protected PublisherStateActive(Publisher<T> ctx) {
			super(ctx);
			log.fine("Publisher state activated: [Scope:" + scope
					+ ",State:Active,Type:" + typeinfo + "]");
		}

		protected void deactivate() {
			router.deactivate();
			p.state = new PublisherStateInactive(p);
			log.info("Publisher deactivated: [Scope:" + scope + ",Type:"
					+ typeinfo + "]");
		}

		protected Event send(Event e) {
			e.setScope(scope);
			e.ensureId();
			router.publishSync(e);
			return e;
		}

		protected Event send(T d) {
			Event e = new Event(typeinfo, (Object) d);
			e.setScope(scope);
			e.ensureId();
			router.publishSync(e);
			return e;
		}

	}

	private void initMembers(Scope u, String t, TransportFactory tfac) {
		state = new PublisherStateInactive(this);
		this.transportFactory = tfac;
		this.scope = u;
		this.typeinfo = t;
		router = new Router(transportFactory, PortConfiguration.OUT);
		log.fine("New publisher instance created: [Scope:" + scope
				+ ",State:Inactive,Type:" + typeinfo + "]");
	}

	public Publisher(Scope scope) {
		initMembers(scope, "string", TransportFactory.getInstance());
	}

	public Publisher(Scope scope, TransportFactory tfac) {
		initMembers(scope, "string", tfac);
	}

	public Publisher(Scope scope, String t) {
		initMembers(scope, t, TransportFactory.getInstance());
	}

	public Publisher(Scope scope, String t, TransportFactory tfac) {
		initMembers(scope, t, tfac);
	}

	/**
	 * Returns the scope of the publisher
	 * 
	 * @return scope of Publisher as a String
	 */
	public Scope getScope() {
		return scope;
	}

	public synchronized void activate() throws InitializeException {
		state.activate();
	}

	public synchronized void deactivate() {
		state.deactivate();
	}

	/**
	 * Send an {@link Event} to all subscribed participants
	 */
	public synchronized Event send(Event e) {
		return state.send(e);
	}

	/**
	 * Send data (of type <T>) to all subscribed participants
	 */
	public synchronized Event send(T d) {
		return state.send(d);
	}

}
