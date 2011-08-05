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

import java.util.Formatter;
import java.util.UUID;
import java.util.logging.Logger;

import rsb.util.UUIDTools;

/**
 * This class serves as a Uniform Resource Name to identify events in an RSB 
 * system. This URN is based on the participant's ID and a sequence number 
 * generated at the sender side unique for this participant. Both can be 
 * combined and returned as a UUID. 
 * Please note, that the sequence number is a 32bit unsigned integer and thus 
 * can overrun in a long-running system. In such cases, the timestamp has to be
 * additionally considered to further distinguish between events. 
 *  
 * @author swrede
 * @author jwienke
 */
public class EventId {
	
	private final static Logger LOG = Logger.getLogger(EventId.class.getName());

	// ID of event generating participant
	protected ParticipantId participantId;

	// Sequence number unique within participant
	protected long sequenceNumber;
	
	// Unique ID
	protected UUID uuid;
	
	/**
	 * Creates a unique Id based on participant
	 * and sequence number.
	 */
	public EventId(ParticipantId participantId, long sequenceNumber2) {
		this.participantId = participantId;
		this.sequenceNumber = sequenceNumber2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((participantId == null) ? 0 : participantId.hashCode());
		result = prime * result
				+ (int) (sequenceNumber ^ (sequenceNumber >>> 32));
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EventId other = (EventId) obj;
		if (participantId == null) {
			if (other.participantId != null) {
				return false;
			}
		} else if (!participantId.equals(other.participantId)) {
			return false;
		}
		if (sequenceNumber != other.sequenceNumber) {
			return false;
		}
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	public UUID getAsUUID() {	
		if (uuid==null) {
			String seqNr = formatSequenceNumber(sequenceNumber);
			LOG.finest("UUID generation for Event with ParticipantId " + participantId.toString() + " and sequence number: " + seqNr);
			uuid = UUIDTools.getNameBasedUUID(participantId.getUUID(),seqNr);
		}
		return uuid;
	}
	
	public static String formatSequenceNumber(long value) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);
		formatter.format("%08x", value);
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Id [participantId=" + participantId + ", sequenceNumber="
				+ sequenceNumber + ", uuid=" + uuid + "]";
	}

}
