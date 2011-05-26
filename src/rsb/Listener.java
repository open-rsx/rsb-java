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

import rsb.event.RSBListener;
import rsb.event.Subscription;
import rsb.filter.ScopeFilter;
import rsb.naming.NotFoundException;
import rsb.transport.PortConfiguration;
import rsb.transport.Router;
import rsb.transport.TransportFactory;

/**
 * This class implements the receiving part of the Inform-Listen (n:m)
 * communication pattern offered by RSB. Upon creation the Listener
 * instance has to be provided with the scope of the channel to listen
 * to. Listener uses the common event handling mechanisms to process
 * incoming messages. Each time a message is received from an
 * Informer, a PublishEvent is raised and passed to all event Handlers
 * associated to the Listener.
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
	}

	public void deactivate() {
		state.deactivate();
	}

	/**
	 * Register an event handler on this Listener to be notified
	 * about incoming events. All received events will be send to
	 * the registered listeners.
	 *
	 * @param l
	 *            the listener instance to be registered
	 */
	public Subscription addListener(@SuppressWarnings("rawtypes") RSBListener l) {
		Subscription sub = new Subscription();
		log.info("subscribing new listener to scope: " + scope);
		sub.appendFilter(new ScopeFilter(scope));
		sub.appendHandler(l);
		// sub.append(new IdentityFilter(publisherUri,
		// IdentityFilter.Type.SENDER_IDENTITY));
		router.subscribe(sub);
		return sub;
	}

	// TODO add addListener with content-based filtering options aka
	// Subscription object

	/**
	 * Remove an event listener from this Listener.
	 *
	 * @param l
	 *            the listener instance to be removed.
	 * @return true if the instance was found and removed
	 */
	public boolean remove(Subscription sub) {
		router.unsubscribe(sub);
		return true;
	}

	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	public Scope getScope() {
		return scope;
	}

}
