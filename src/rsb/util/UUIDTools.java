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
package rsb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * UUID helper functions.
 * 
 * @author swrede
 *
 */
public class UUIDTools {   
	
	private static MessageDigest digester = null;
	
	/**
	 * Generates name-based URIs according to Version 5 (SHA-1).
	 * 
	 * @param namespace	Namespace UUID
	 * @param name		Actual name to be encoded in UUID 
	 * @return			Byte buffer with V5 UUID
	 */
	public synchronized static UUID getNameBasedUUID(UUID namespace, String name) {	
		// hash initialization
		if (digester==null) {
			try {
				digester = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException nex) {
				throw new RuntimeException("Couldn't instantiate SHA-1 algorithm: "+nex.toString());
			}			
		}
		
		// the actual hashing
		digester.reset();
		digester.update(UUIDTools.toByteArray(namespace));
		digester.update(name.getBytes());
		
		byte[] bytes = Arrays.copyOfRange(digester.digest(), 0, 16);
		
		  
		bytes = setVersionAndVariant(bytes);
		
		// mask byte and set corresponding bits
		// select clock_seq_hi_and_reserved (octet 8)		
		
		// return byte buffer
		return fromByteArray(bytes);
	}

	/**
	 * Updates byte vector with version 5 information and
	 * return the updated byte array.
	 * 
	 */
	private static byte[] setVersionAndVariant(byte[] bytes) {
		// Constants and bit manipulation from:
		// http://svn.apache.org/repos/asf/commons/sandbox/id/trunk/		
		/** Byte position of the clock sequence and reserved field */
	    final short TIME_HI_AND_VERSION_BYTE_6 = 6;
	    /** Byte position of the clock sequence and reserved field */
	    final short CLOCK_SEQ_HI_AND_RESERVED_BYTE_8 = 8;			
	    
	    /** Version five constant for indicating UUID version */
	    // Different to above mentioned code, otherwise version
	    // information is still misleading (set to 3)
	    final int VERSION_FIVE = 5;				
		bytes[TIME_HI_AND_VERSION_BYTE_6] &= 0x0F;
		bytes[TIME_HI_AND_VERSION_BYTE_6] |= (VERSION_FIVE << 4);
		
		//Set variant
		bytes[CLOCK_SEQ_HI_AND_RESERVED_BYTE_8] &= 0x3F; //0011 1111
		bytes[CLOCK_SEQ_HI_AND_RESERVED_BYTE_8] |= 0x80; //1000 0000		
		
		return bytes;
	}
	
	/**
	 * Creates an ID from a byte representation.
	 * 
	 * @param bytes
	 *            byte representation of the id.
	 */
	public static UUID fromByteArray(byte[] bytes) {

		assert bytes.length == 16;

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (bytes[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (bytes[i] & 0xff);
		
		return new UUID(msb, lsb);
	}	
	
	/**
	 * Returns the bytes representing the id.
	 * 
	 * @return byte representing the id (length 16)
	 */
	public static byte[] toByteArray(UUID id) {

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
	
}
