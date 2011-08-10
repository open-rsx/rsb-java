/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import rsb.Event;
import rsb.RSBException;

/**
 * @author swrede
 * @param <T>
 * @param <U>
 *
 */
public class RemoteDataMethod<T, U> extends AbstractRemoteMethod<T, U> {

	public RemoteDataMethod(Server server, String name) {
		super(server, name);
	}
	
	@Override
	public Future<T> call(U data) throws RSBException {
		// build event and send it over the informer as request
		final Event request = new Event(data.getClass());
		request.setData(data);
		return sendRequest(request);	
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void completeRequest(Future<T> request, Event event) {
		request.complete((T) event.getData()); 
	}

}
