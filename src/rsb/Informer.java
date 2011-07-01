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
import rsb.transport.TransportFactory;

/**
 * This class offers a method to publish events to a channel, reaching all
 * participating Listeners. This n:m-communication is one of the basic
 * communication patterns offered by RSB.
 * 
 * @author swrede
 * @author rgaertne
 * @author jschaefe
 * @author jwienke
 */
public class Informer<T> extends Participant {

	private final static Logger log = Logger
			.getLogger(Informer.class.getName());

	/** state variable for publisher instance */
	protected InformerState<T> state;

	/** default data type for this publisher */
	protected String typeinfo;

	protected class InformerStateInactive extends InformerState<T> {

		protected InformerStateInactive(Informer<T> ctx) {
			super(ctx);
			log.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Inactive,Type:" + typeinfo + "]");
		}

		protected void activate() throws InitializeException {
			getRouter().activate();
			p.state = new InformerStateActive(p);
			log.info("Informer activated: [Scope:" + getScope() + ",Type:"
					+ typeinfo + "]");
		}

	}

	protected class InformerStateActive extends InformerState<T> {

		protected InformerStateActive(Informer<T> ctx) {
			super(ctx);
			log.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Active,Type:" + typeinfo + "]");
		}

		protected void deactivate() {
			getRouter().deactivate();
			p.state = new InformerStateInactive(p);
			log.info("Informer deactivated: [Scope:" + getScope() + ",Type:"
					+ typeinfo + "]");
		}

		protected Event send(Event e) throws RSBException {

			if (!typeinfo.equals(e.getType())) {
				throw new IllegalArgumentException(
						"Event type must not be null.");
			}
			if (!getScope().equals(e.getScope())) {
				throw new IllegalArgumentException(
						"Event scope must not be null.");
			}

			e.getMetaData().setSenderId(getId());
			getRouter().publishSync(e);
			return e;

		}

		protected Event send(T d) throws RSBException {
			Event e = new Event(getScope(), typeinfo, (Object) d);
			return send(e);
		}

	}

	private void initMembers(String t) {
		state = new InformerStateInactive(this);
		this.typeinfo = t;
		log.fine("New publisher instance created: [Scope:" + getScope()
				+ ",State:Inactive,Type:" + typeinfo + "]");
	}

	Informer(Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers("string");
	}

	Informer(Scope scope, TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers("string");
	}

	Informer(Scope scope, String t) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(t);
	}

	Informer(Scope scope, String t, TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(t);
	}

	public synchronized void activate() throws InitializeException {
		state.activate();
	}

	public synchronized void deactivate() {
		state.deactivate();
	}

	/**
	 * Send an {@link Event} to all subscribed participants.
	 * 
	 * @param e
	 *            the event to send
	 * @return modified event with set timing information
	 * @throws RSBException
	 *             error sending event
	 * @throws IllegalArgumentException
	 *             if the event is not complete or does not match the type or
	 *             scope settings of the informer
	 */
	public synchronized Event send(Event e) throws RSBException {
		return state.send(e);
	}

	/**
	 * Send data (of type <T>) to all subscribed participants.
	 * 
	 * @param d
	 *            data to send with default setting from the informer
	 * @return generated event
	 * @throws RSBException
	 *             error sending event
	 */
	public synchronized Event send(T d) throws RSBException {
		return state.send(d);
	}

	/**
	 * Returns the string describing the type of data sent by this informer.
	 * 
	 * @return string declarator
	 */
	public String getTypeInfo() {
		return typeinfo;
	}

	@Override
	public boolean isActive() {
		return state.getClass() == InformerStateActive.class;
	}

}
