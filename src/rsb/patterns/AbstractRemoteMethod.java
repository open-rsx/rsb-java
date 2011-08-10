/**
 * ============================================================
 *
 * This file is part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.RSBException;
import rsb.filter.MethodFilter;

/**
 * Objects of this class represent methods provided by a remote
 * server.
 *
 * @author jmoringe
 * @author swrede
 *
 * @param <T>	return type 
 * @param <U>	parameter object type 
 *
 */
public abstract class AbstractRemoteMethod<T, U> extends Method implements Handler {

	private static final Logger LOG = Logger.getLogger(AbstractRemoteMethod.class.getName()); 

	protected class CollectorThread implements Runnable {
		public void run() {
			do {
				for (String key : pendingRequests.keySet()) {
					if (pendingRequests.get(key).isEnqueued()) {
						// future already cleared, thus we remove the pending request entry
						LOG.info("Removing request with id: " + key);
						pendingRequests.remove(key);
					}
				}
			} while (isActive());
		}
	}

	// assuming usually eight threads will write simultaneously to the map
	private final Map<String, WeakReference<Future<T>>> pendingRequests = new ConcurrentHashMap<String, WeakReference<Future<T>>>(16,0.75f,8);

	/**
	 * Create a new RemoteMethod object that represent the remote method named @a
	 * name provided by @a server.
	 *
	 * @param server
	 *            The remote server providing the method.
	 * @param name
	 *            The name of the method.
	 */
	public AbstractRemoteMethod(final Server server, final String name) {
		super(server, name);
		listener = factory.createListener(REPLY_SCOPE);
		informer = factory.createInformer(REQUEST_SCOPE);
		listener.addFilter(new MethodFilter("REPLY"));
		listener.addHandler(this, true);
	}
	
	public abstract Future<T> call(final U data) throws RSBException;

	/**
	 * @param event
	 * @return
	 * @throws RSBException
	 */
	protected Future<T> sendRequest(final Event event) throws RSBException {
		// set metadata
		event.setScope(REQUEST_SCOPE);
		event.setMethod("REQUEST");
		// further metadata is set by informer
		
		// instantiate future
		final Future<T> future = new Future<T>();
		Event sentEvent = null;
		synchronized (this) {
			sentEvent = informer.send(event);
			// put future with id as weak ref in pending results table
			pendingRequests.put(sentEvent.getId().getAsUUID().toString(), new WeakReference<Future<T>>(future));
			LOG.fine("registered future in pending requests with id: " + sentEvent.getId().getAsUUID().toString());
		}
		return future;
	}

	@Override
	public void internalNotify(final Event event) {
		//Thread.dumpStack();
		String replyId = null;
		// get reply id
		try {
			replyId = event.getMetaData().getUserInfo("rsb:reply");
			LOG.fine("Received reply with id: " + replyId);
		} catch (IllegalArgumentException exception) {
			LOG.warning("Received reply event without rsb:reply userinfo item. Skipping it.");
			return;
		}

		// check for reply id in list of pending calls
		Future<T> request = null;
		synchronized (this) {
			if (pendingRequests.containsKey(replyId)) {
				request = pendingRequests.get(replyId).get();
				if (request==null) {
					// the future was already garbage collected locally
					LOG.info("Can not deliver reply: Future reference already garbage collected in user code!");
				}
				pendingRequests.remove(replyId);
			}
		}
		// error cases
		if (request==null) {
			// for instance, if several clients send an event to the same server method,
			// this effect may occur
			LOG.info("Could not find matching reply in table for id: " + replyId);
		} else {
			LOG.fine("Found plending reply for id: " + replyId);
			try {
				// an error occured at the server side
				if (event.getMetaData().getUserInfo("rsb:error?")!=null) {
					final String error = (String) event.getData();
					request.error(new RSBException(error));
					return;
				}
			} catch (IllegalArgumentException exception) {
				// ignore
			}
			completeRequest(request,event);
		}
	}

	protected abstract void completeRequest(Future<T> future, Event event);
	
};