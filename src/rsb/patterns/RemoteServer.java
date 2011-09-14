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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

/**
 * Objects of this class represent remote servers in a way that allows
 * calling methods on them as if they were local.
 *
 * @author jmoringe
 * @author swrede
 *
 */
public class RemoteServer extends Server {

	private static final Logger LOG = Logger.getLogger(RemoteServer.class.getName());

    private double timeout;

	/**
	 * Create a new RemoteServer object that provides its methods under the
	 * scope @a scope.
	 *
	 * @param scope
	 *            The common super-scope under which the methods of the remote
	 *            created server are provided.
	 * @param timeout
	 *            The amount of seconds methods calls should wait for their
	 *            replies to arrive before failing.
	 */
	public RemoteServer(Scope scope, double timeout) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
		this.timeout = timeout;
	}

	/**
	 * Create a new RemoteServer object that provides its methods under the
	 * scope @a scope.
	 *
	 * @param scope
	 *            The common super-scope under which the methods of the remote
	 *            created server are provided.
	 */
	public RemoteServer(Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
		this.timeout = 25;
	}

	public double getTimeout() {
		return timeout;
	}

	public Future<Event> callAsync(final String name, final Event event) throws RSBException {
		return callAsyncInternal(name,event,true);
	}

	/**
	 * Async call returning an rsb.patterns.Future object
	 *
	 * @param name
	 * @param data
	 * @return
	 * @throws RSBException
	 */
	public <T, U> Future<T> callAsync(final String name, final U data) throws RSBException {
		return callAsyncInternal(name, data,false);
	}

	@SuppressWarnings("unchecked")
	private <T, U> Future<T> callAsyncInternal(final String name, final U data, boolean isEvent) throws RSBException {
		AbstractRemoteMethod<T, U> method = null;
		// get method, either new or cached
		if (!methods.containsKey(name)) {
			try {
				method = (AbstractRemoteMethod<T, U>) addMethod(name,isEvent);
			} catch (InitializeException exception) {
				LOG.warning("Exception during method activation: " + exception.getMessage() + " Re-throwing it.");
				throw new RSBException(exception);
			}
		} else {
			try {
				method = (AbstractRemoteMethod<T, U>) methods.get(name);
			} catch (ClassCastException exception) {
				LOG.warning("Exception during method activation: " + exception.getMessage() + " Re-throwing it.");
				throw new RSBException(exception);
			}
		}
//			if (isEvent) {
//				return method.callA((Event) data);
//			} else {
//				return method.callAsyncData(data);
//			}
		return method.call(data);
	}

	public Event call(final String name, final Event event) throws RSBException {
		Event result = callInternal(name, event, true);
		return result;
	}

	/**
	 * Blocking call directly returning the data or throwing an
	 * exception upon timeout, interruption or failure.
	 *
	 * @param name
	 * @param data
	 * @return
	 * @throws RSBException
	 */
	public <U, T> U call(final String name, final T data) throws RSBException {
		return this.<U, T>callInternal(name, data, false);
	}

	// internal methods are to prevent recursive calls from call(string, event) to call(string, event), which
	// are bound to occur as the originally intended target (the template) method is not called in that situation.
	private <U, T> U callInternal(final String name, final T data, boolean isEvent) throws RSBException {
		Future<U> future = callAsyncInternal(name, data,isEvent);
		U result;
		try {
			result = future.get((long) timeout,TimeUnit.SECONDS);
		} catch (InterruptedException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		} catch (ExecutionException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		} catch (TimeoutException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		}
		return result;
	}

	protected <U, T> AbstractRemoteMethod<?, ?> addMethod(String name, boolean isEvent)
			throws InitializeException {
		LOG.fine("Registering new method " + name);
		AbstractRemoteMethod<?, ?> method;
		if (isEvent) {
			method = new RemoteEventMethod(this, name);
		} else {
			method = new RemoteDataMethod<T, U>(this, name);
		}
		methods.put(name, method);

		if (this.isActive()) {
			method.activate();
		}

		return method;
	}

};
