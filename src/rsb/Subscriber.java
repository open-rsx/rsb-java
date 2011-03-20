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

import rsb.event.RSBEventListener;
import rsb.event.RSBListener;
import rsb.event.Subscription;
import rsb.filter.ScopeFilter;
import rsb.naming.NotFoundException;
import rsb.transport.PortConfiguration;
import rsb.transport.Router;
import rsb.transport.TransportFactory;

/**
 * This class implements the receiving part of the Publish-Subscribe (1:m)
 * communication pattern offered by XCF. Upon creation the Subscriber instance
 * has to be provided with the name of the Publisher instance to register at.
 * Subscriber uses the common event handling mechanisms to process incoming
 * messages. Each time a message is received from the Publisher, a PublishEvent
 * is raised and passed to all event listeners registered on the Subscriber. 
 * 
 * @author swrede
 * @author jschaefe
 *
 */

public class Subscriber implements RSBObject {
	
	
	protected class SubscriberStateActive extends SubscriberState {
		public SubscriberStateActive(Subscriber ctx) {
			super(ctx);
		}
		
		protected void deactivate() {
			router.deactivate();
			s.state = new SubscriberStateInactive(s);
		}
		
	}

	
	
	protected class SubscriberStateInactive extends SubscriberState {

		public SubscriberStateInactive(Subscriber ctx) {
			super(ctx);
		}

		protected void activate() throws InitializeException {
			router.activate();
			s.state = new SubscriberStateActive(s);
		}

	}

	protected static Logger log = Logger.getLogger(Subscriber.class.getName());

	/* URI of subscriber */
	protected String subscriberUri;	
	
	/* URI of publisher */
	protected String publisherUri;
	
	/* class state variable */
	protected SubscriberState state;
	
	protected ErrorHandler errorHandler;
	
	/* transport factory */
	TransportFactory transportFactory;
	
	/* router to access transport layer */
	Router router;

	public Subscriber(String su, String pu, TransportFactory tfac) {
		this.state = new SubscriberStateInactive(this);
		this.transportFactory = tfac;
		this.router = new Router(tfac,PortConfiguration.IN);
		errorHandler = new DefaultErrorHandler(log);
		this.subscriberUri = su;
		this.publisherUri = pu;

		// set own URI (configured scope + publisher name)
		// this.uri = new XcfUri(Configurator.getInstance().getReferenceLocalUri(publisherName));
		log.info("New subscriber instance: [sURI=" + subscriberUri + ",pURI=" + publisherUri + "]");		
	}

	public void activate() throws InitializeException, NotFoundException {
		state.activate();
	}

	public void deactivate() {
		state.deactivate();
	}

	/**
	 * Register an event listener on this Subscriber to be notified about
	 * published messages. All messages received from the Publisher will be
	 * send to the registered listeners.
	 * @param l the listener instance to be registered
	 */
	@SuppressWarnings("unchecked")
	public Subscription addListener(RSBListener l) {
		Subscription sub = new Subscription();
		log.info("subscribing new listener to url: " + publisherUri);
		sub.appendFilter(new ScopeFilter(publisherUri));
		sub.appendHandler(l);
		// sub.append(new IdentityFilter(publisherUri, IdentityFilter.Type.SENDER_IDENTITY));
		router.subscribe(sub);
		return sub;
	}
	
	// TODO add addListener with content-based filtering options aka Subscription object

	/**
	 * Remove an event listener from this Subscriber.
	 * @param l the listener instance to be removed.
	 * @return true if the instance was found and removed
	 */
	@SuppressWarnings("unchecked")
	public boolean removeListener(RSBEventListener l) {
		// TODO put Subscription into the listener or in a map?
		// router.unsubscribe(sub, sink);
		return false;
	}

	/**
	 * Returns a <code>SynchronizedQueue</code> (or a
	 * <code>ReceiveLastQueue</code>) which is already registered on this
	 * <code>Subscriber</code> as event listener. The queue stores the
	 * <code>XOPData</code> contents of the recieved <code>PublishEvents</code>
	 * and returns them upon calls to <code>next()</code> and the like.
	 * 
	 * @param recieveLast whether to return a <code>RecieveLastQueue</code> or a
	 * <code>SynchronizedQueue</code>
	 * @return a queue object which is already registered on this
	 * <code>Subscriber</code> as event listener.
	 */
//	public SynchronizedQueue<XcfEvent> getQueue(boolean receiveLast) {
//		SynchronizedQueue<XcfEvent> q = null;
//		if(receiveLast) {
//			q = new ReceiveLastQueue<XcfEvent>() {
//				@Override
//				public XcfEvent convertFromXcfEvent(XcfEvent e) {
//					return e;
//				}
//			};
//		} else {
//			q = new SynchronizedQueue<XcfEvent>() {
//				@Override
//				public XcfEvent convertFromXcfEvent(XcfEvent e) {
//					log.debug("received event: " + e.getData().getDocumentAsText(true));
//					return e;
//				}
//			};
//		}
//		QueueAdapter qa = new QueueAdapter(q);
//		this.addListener(qa);		
//		return q;
//	}
	
	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}
	
	public String getPublisherUri() {
		return publisherUri;
	}
	
}
