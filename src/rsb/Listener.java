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
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import rsb.filter.Filter;
import rsb.filter.ScopeFilter;
import rsb.naming.NotFoundException;
import rsb.transport.PortConfiguration;
import rsb.transport.Router;
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
 */
public class Listener implements RSBObject {

	protected class ListenerStateActive extends ListenerState {
		public ListenerStateActive(Listener ctx) {
			super(ctx);
		}

		protected void deactivate() {
			router.deactivate();
			s.state = new ListenerStateInactive(s);
		}

	}

	protected class ListenerStateInactive extends ListenerState {

		public ListenerStateInactive(Listener ctx) {
			super(ctx);
		}

		protected void activate() throws InitializeException {
			router.activate();
			s.state = new ListenerStateActive(s);
		}

	}

	protected static Logger log = Logger.getLogger(Listener.class.getName());

	/** scope of channel */
	protected Scope scope;

	/** class state variable */
	protected ListenerState state;

	protected ErrorHandler errorHandler;

	/* transport factory */
	TransportFactory transportFactory;

	/* router to access transport layer */
	Router router;

	ArrayList<Filter> filters = new ArrayList<Filter>();
	ArrayList<Handler<Event>> handlers = new ArrayList<Handler<Event>>();

	public Listener(Scope scope) {
		initMembers(scope, TransportFactory.getInstance());
	}

	public Listener(Scope scope, TransportFactory tfac) {
		initMembers(scope, tfac);
	}

	/**
	 * @param scope
	 * @param tfac
	 */
	protected void initMembers(Scope scope, TransportFactory tfac) {
		this.state = new ListenerStateInactive(this);
		this.transportFactory = tfac;
		this.router = new Router(tfac, PortConfiguration.IN);
		errorHandler = new DefaultErrorHandler(log);
		this.scope = scope;
		log.info("New Listener instance: [scope=" + scope + "]");
	}

	public void activate() throws InitializeException, NotFoundException {
		state.activate();
		router.addFilter(new ScopeFilter(scope)); // TODO probably breaks
													// re-activation
	}

	public void deactivate() {
		state.deactivate();
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public Iterator<Filter> getFilterIterator() {
		Iterator<Filter> it = filters.iterator();
		return it;
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
		router.addFilter(filter);
	}

	public List<Handler<Event>> getHandlers() {
		return handlers;
	}

	public Iterator<Handler<Event>> getHandlerIterator() {
		Iterator<Handler<Event>> it = handlers.iterator();
		return it;
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
	public void addHandler(Handler<Event> handler, boolean wait) {
		handlers.add(handler);
		router.addHandler(handler);
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
	 */
	public void removeHandler(Handler<Event> handler, boolean wait) {
		handlers.remove(handler);
		router.removeHandler(handler);
	}

	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	public Scope getScope() {
		return scope;
	}

}
