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

import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.DefaultConverterRepository;
import rsb.transport.PortConfiguration;
import rsb.transport.SequenceNumber;
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
public class Informer<T extends Object> extends Participant {

	private final static Logger LOG = Logger
			.getLogger(Informer.class.getName());

	/** state variable for publisher instance */
	protected InformerState<T> state;
	
	/** atomic uint32 counter object for event sequence numbers */
	protected SequenceNumber sequenceNumber = new SequenceNumber();
	
	/** converter repository for type mappings */
	protected ConverterSelectionStrategy<?> converter;

	/** default data type for this publisher */
	protected Class<?> type;

	protected class InformerStateInactive extends InformerState<T> {

		protected InformerStateInactive(Informer<T> ctx) {
			super(ctx);
			LOG.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Inactive,Type:" + type.getName() + "]");
		}

		protected void activate() throws InitializeException {
			// TODO check where to put the type mangling java type to wire type
			// probably this should be translated in the respective converter itself
			// however: this information is part of the notification! so, that is a problem
			converter = DefaultConverterRepository.getDefaultConverterRepository().getConvertersForSerialization();
			getRouter().activate();
			ctx.state = new InformerStateActive(ctx);
			LOG.info("Informer activated: [Scope:" + getScope() + ",Type:"
					+ type.getName() + "]");
		}

	}

	protected class InformerStateActive extends InformerState<T> {

		protected InformerStateActive(Informer<T> ctx) {
			super(ctx);
			LOG.fine("Informer state activated: [Scope:" + getScope()
					+ ",State:Active,Type:" + type.getName() + "]");
		}

		protected void deactivate() {
			getRouter().deactivate();
			ctx.state = new InformerStateInactive(ctx);
			LOG.info("Informer deactivated: [Scope:" + getScope() + ",Type:"
					+ type.getName() + "]");
		}

		protected Event send(Event e) throws RSBException {
			
			if (!getScope().equals(e.getScope())) {
				throw new IllegalArgumentException(
						"Event scope must not be null.");
			}
			if (e.getType()==null) {
				throw new IllegalArgumentException(
						"Event type must not be null.");
			}			
			// TODO check performance of isAssignableFrom	
			if (!ctx.getTypeInfo().isAssignableFrom(e.getType())) {
				throw new IllegalArgumentException(
						"Type of event data does not match nor is a sub-class of the Informer data type.");
			}
			
			// set participant metadata
			// increment atomic counter
			e.setSequenceNumber(sequenceNumber.incrementAndGet());
			e.setSenderId(getId());
			
			// send to transport(s)
			getRouter().publishSync(e);
			
			// return event for local use
			return e;

		}

		protected Event send(T d) throws RSBException {
			Event e = new Event(getScope(), d.getClass(), (Object) d);
			return send(e);
		}

	}

	private void initMembers(Class<?> c) {
		this.type = c;
		state = new InformerStateInactive(this);
		LOG.fine("New informer instance created: [Scope:" + getScope()
				+ ",State:Inactive,Type:" + type.getName() + "]");
	}

	Informer(Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(Scope scope, TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(Object.class);
	}

	Informer(Scope scope, Class<?> type) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.OUT);
		initMembers(type);
	}

	Informer(Scope scope, Class<?> type, TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.OUT);
		initMembers(type);
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
	 * Returns the class describing the type of data sent by this informer.
	 * 
	 * @return string declarator
	 */
	public Class<?> getTypeInfo() {
		return type;
	}

	/**
	 * Set the class object describing the type of data sent by this informer.
	 * 
	 * @return string declarator
	 */
	public void setTypeInfo(Class<?> typeInfo) {
		type = typeInfo;
	}	
	
	@Override
	public boolean isActive() {
		return state.getClass() == InformerStateActive.class;
	}

}
