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
 *
 */
public class RemoteEventMethod extends AbstractRemoteMethod<Event, Event> {

	public RemoteEventMethod(Server server, String name) {
		super(server, name);
	}

	@Override	
	public Future<Event> call(final Event event) throws RSBException {
		return sendRequest(event);
	}	
	
	@Override
	protected void completeRequest(Future<Event> request, Event event) {
		request.complete(event);	
	}

}
