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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import rsb.filter.Filter;
import rsb.filter.ScopeFilter;
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

	protected class ListenerStateActive extends ListenerState {
		public ListenerStateActive(Listener ctx) {
			super(ctx);
		}

		protected void deactivate() {
			getRouter().deactivate();
			s.state = new ListenerStateInactive(s);
		}

	}

	protected class ListenerStateInactive extends ListenerState {

		public ListenerStateInactive(Listener ctx) {
			super(ctx);
		}

		protected void activate() throws InitializeException {
			getRouter().activate();
			s.state = new ListenerStateActive(s);
		}

	}

	protected final static Logger LOG = Logger.getLogger(Listener.class.getName());

	/** class state variable */
	private ListenerState state;

	@SuppressWarnings({ "deprecation", "unused" })
	private ErrorHandler errorHandler;

	private ArrayList<Filter> filters = new ArrayList<Filter>();
	private ArrayList<Handler> handlers = new ArrayList<Handler>();

	Listener(Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
		initMembers(scope);
	}

	Listener(Scope scope, TransportFactory tfac) {
		super(scope, tfac, PortConfiguration.IN);
		initMembers(scope);
	}

	/**
	 * @param scope
	 *            scope of this listener.
	 */
	private void initMembers(Scope scope) {
		this.state = new ListenerStateInactive(this);
		errorHandler = new DefaultErrorHandler(LOG);
		LOG.info("New Listener instance: [scope=" + scope + "]");
	}

	public void activate() throws InitializeException {
		state.activate();
		// TODO probably breaks re-activation
		getRouter().addFilter(new ScopeFilter(getScope()));
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
		getRouter().addFilter(filter);
	}

	public List<Handler> getHandlers() {
		return handlers;
	}

	public Iterator<Handler> getHandlerIterator() {
		Iterator<Handler> it = handlers.iterator();
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
	public void addHandler(Handler handler, boolean wait) {
		handlers.add(handler);
		getRouter().addHandler(handler, wait);
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
	public void removeHandler(Handler handler, boolean wait)
			throws InterruptedException {
		handlers.remove(handler);
		getRouter().removeHandler(handler, wait);
	}

	/**
	 * @deprecated not yet designed
	 */
	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	@Override
	public boolean isActive() {
		return state.getClass() == ListenerStateActive.class;
	}

}
