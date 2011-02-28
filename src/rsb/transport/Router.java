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

import java.util.logging.Logger;

import rsb.EventProcessor;
import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.RSBException;

public class Router {

	private final static Logger log = Logger.getLogger(Router.class.getName());

	protected Port pi;
	protected Port po;
	protected EventProcessor epi;
	protected EventProcessor epo;
	//protected EventQueue eq = new EventQueue();

	public Router(TransportFactory f) {
    	// router setup
		pi = (Port) f.createPort();
		po = (Port) f.createPort();
    	//epi = new EventProcessor("EP In ["+this.toString()+"]",pi);
    	//epi.addObserver(pi);
    	//epo = new EventProcessor("EP Out ["+this.toString()+"]",eq);
    	//epo.addObserver(po);
    	//Subscription os = new Subscription();
    	// default out port
    	//subscribe(os,po);
	}
	
	public void activate() throws InitializeException {
		try {
			pi.activate();
			po.activate();
		} catch (RSBException e) {
			log.severe("exception occured during port initialization for router");
			throw new InitializeException(e); 
		}		
		//epi.start();
		//epo.start();
	}
	
	/**
	 * Publish an RSBEvent over the event bus.
	 * @param e The RSBEvent to be published
	 */
	public void publish(RSBEvent e) {
		// send event async
		throw new RuntimeException("Router::publish method not implemented!");
	}
	
	public void publishSync(RSBEvent e) {
		// send event sync?
		log.finest("Router publishing new event to port: [EventID:"+e.getId().toString()+",PortType:"+po.getType()+"]");
		po.push(e);
	}
	
	/**
	 * Subscribes a listener to all events that pass it's filter chain.
	 * @param l The XcfEventListener to be subscribed.
	 */
	// TODO check whether we shall allow to register here an EventSink not a listener
//	public void subscribe(Subscription sub, XcfEventListener listener) {
//		epi.add(sub,listener);
//	}
//	
//	public void subscribe(Subscription sub, Port p) {
//		epo.add(sub,p);
//	}

	/**
	 * Subscribes a listener to all events that pass it's filter chain.
	 * @param l The XcfEventListener to be subscribed.
	 */	
//	public void unsubscribe(Subscription sub, XcfEventListener sink) {
//		epi.remove(sub, sink);
//	}
//	
//	public void unsubscribe(Subscription sub, Port p) {
//		epo.remove(sub, p);
//	}

	public void deactivate() {
		try {
			pi.deactivate();
			po.deactivate();
		} catch (RSBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
//		epi.interrupt();
//		epo.interrupt();
//		try {
//			epi.join();
//			epo.join();
//		} catch (InterruptedException e) {
//			log.warn("Router was interrupted trying to join it's event dispatcher threads");
//		}
	}
	
}
