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
 *
 */
public class Publisher<T> implements RSBObject {
	
	private final static Logger log = Logger.getLogger(Publisher.class.getName()); 

	/* publisher's name within it's scope */
	protected String uri;

	/* state variable for publisher instance */
	protected PublisherState<T> state;

	/* transport factory object */
	protected TransportFactory transportFactory;
	
	/* default data type for this publisher */
	protected String typeinfo; 
	
	/* transport router */
	protected Router router;

	protected class PublisherStateInactive extends PublisherState<T> {

		protected PublisherStateInactive(Publisher<T> ctx) {
			super(ctx);
		}
		
		protected void activate() throws InitializeException {
			router.activate();
			p.state = new PublisherStateActive(p);
		}
		
	}
	
	protected class PublisherStateActive extends PublisherState<T> {

		protected PublisherStateActive(Publisher<T> ctx) {
			super(ctx);
		}
		
		protected void deactivate() {
			router.deactivate();
			p.state = new PublisherStateInactive(p);
		}

		protected RSBEvent send(RSBEvent e) {		
			e.setUri(uri);
			e.ensureID();
			router.publishSync(e);
			return e;
		}
		
		protected RSBEvent send(T d) {
			RSBEvent e = new RSBEvent(typeinfo,(Object) d);
			e.setUri(uri);
			e.ensureID();
			router.publishSync(e);
			return e;
		}		
		
	}

	private void initMembers(String u, String t, TransportFactory tfac) {
		state = new PublisherStateInactive(this);
		this.transportFactory = tfac;
		this.uri = u;
		this.typeinfo = t;
		router = new Router(transportFactory);
		log.info("New publisher instance created: [URI:" + uri + ",State: Inactive]"); 		
	}

	public Publisher(String u) {		 
		initMembers(u, "string", TransportFactory.getInstance());
	}
	
	public Publisher(String u, TransportFactory tfac) {		 
		initMembers(u, "string", tfac);
	}	
	
	public Publisher(String u, String t) {		 
		initMembers(u, t, TransportFactory.getInstance());
	}	
	
	public Publisher(String u, String t, TransportFactory tfac) {
		initMembers(u, t, tfac);
	}

	/**
	 * Returns URI of the publisher
	 * @return URI of Publisher as a String
	 */
	public String getURI() {
		return uri;
	}
	
	public synchronized void activate() throws InitializeException {
		state.activate();
	}
	
	public synchronized void deactivate() {
		state.deactivate();
	}
	
	/**
	 * Send an RSBEvent to all subscribed participants 
	 */
	public synchronized RSBEvent send(RSBEvent e) {
		return state.send(e);
	}

	/**
	 * Send data (of type <T>) to all subscribed participants 
	 */
	public synchronized RSBEvent send(T d) {
		return state.send(d);
	}
	
	
}
	

