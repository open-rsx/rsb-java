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
package rsb;

import java.util.UUID;

/**
 * This class serves as a Uniform Resource Name to identify participants 
 * in an RSB system. At present, the URN is based on an UUID that is 
 * unique for each participant instance.
 * 
 * @author swrede
 * @author jwienke
 */
public class ParticipantId {
	
	private UUID id;

	/**
	 * Creates a new random id.
	 */
	public ParticipantId() {
		id = UUID.randomUUID();
	}

	/**
	 * Creates an ID from a byte representation.
	 * 
	 * @param bytes
	 *            byte representation of the id.
	 */
	public ParticipantId(byte[] bytes) {

		assert bytes.length == 16;

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (bytes[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (bytes[i] & 0xff);
		this.id = new UUID(msb, lsb);

	}

	/**
	 * Parses an id from its string form generated with {@link #toString()}.
	 * 
	 * @param sid
	 *            string representation
	 * @throws IllegalArgumentException
	 *             invalid string format
	 */
	public ParticipantId(String sid) {
		id = UUID.fromString(sid);
	}

	@Override
	public String toString() {
		return id.toString();
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParticipantId other = (ParticipantId) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}



}
