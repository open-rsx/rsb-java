/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.NotificationType.Notification;

/**
 * @author swrede
 *
 */
public class EventBuilder {

	private static Logger log = Logger.getLogger(EventBuilder.class.getName());	
	
	/** 
	 * Build event from RSB Notification. Excludes user data  
	 * de-serialization as it is bound to the converter configuration.
	 * 
	 */
	public static Event fromNotification(Notification n) {
		log.fine("decoding notification");
		Event e = new Event();
		e.setScope(new Scope(n.getScope().toStringUtf8()));
		e.setId(new ParticipantId(n.getEventId().getSenderId()
				.toByteArray()), n.getEventId().getSequenceNumber());
		if (n.hasMethod()) {
			e.setMethod(n.getMethod().toStringUtf8());
		}
		
		log.finest("returning event with id: " + e.getId());

		// metadata
		e.getMetaData().setCreateTime(n.getMetaData().getCreateTime());
		e.getMetaData().setSendTime(n.getMetaData().getSendTime());
		e.getMetaData().setReceiveTime(0);
		for (UserInfo info : n.getMetaData().getUserInfosList()) {
		    e.getMetaData().setUserInfo(info.getKey().toStringUtf8(),
						info.getValue().toStringUtf8());
		}
		for (UserTime time : n.getMetaData().getUserTimesList()) {
			e.getMetaData().setUserTime(time.getKey().toStringUtf8(),
					time.getTimestamp());
		}

		// causes
		for (EventId cause : n.getCausesList()) {
			e.addCause(new rsb.EventId(new ParticipantId(cause
					.getSenderId().toByteArray()), cause
					.getSequenceNumber()));
		}	
		
		return e;
	}
	
}
