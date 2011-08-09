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

/**
 * @author swrede
 *
 */
public class ReplyEventCallback implements EventCallback {

	/* (non-Javadoc)
	 * @see rsb.patterns.EventCallback#invoke(rsb.Event)
	 */
	@Override
	public Event invoke(Event request) throws Throwable {
		Event reply = new Event(String.class);
		reply.setData(request.getData());
		reply.getMetaData().setUserInfo("replyTo", request.getId().getAsUUID().toString());
		return reply;
	}

}
