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

import java.util.UUID;

/**
 * This class serves as a Uniform Resource Name to identify events in an RSB
 * system. At present, the URN is based on an UUID that shall is unique for each
 * event instance.
 * 
 * @author swrede
 */
public class EventId {

	UUID id;

	/**
	 * 
	 */
	public EventId() {
		id = UUID.randomUUID();
	}

	public EventId(UUID u) {
		this.id = u;
	}

	public EventId(byte[] bytes) {

		assert bytes.length == 16;

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (bytes[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (bytes[i] & 0xff);
		this.id = new UUID(msb, lsb);

	}

	public EventId(String sid) {
		// id = UUID.fromString(sid.substring(8));
		id = UUID.fromString(sid);
	}

	public String toString() {
		// return "rsb:eid:"+id.toString();
		return id.toString();
	}

	public static EventId generateId() {
		return new EventId();
	}

	public UUID get() {
		return id;
	}

	/**
	 * Returns the bytes representing the id.
	 * 
	 * @return byte representing the id (length 16)
	 */
	public byte[] toByteArray() {

		long msb = id.getMostSignificantBits();
		long lsb = id.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EventId)) {
			return false;
		}
		EventId id = (EventId) obj;
		return this.id.equals(id.id);
	}
}
