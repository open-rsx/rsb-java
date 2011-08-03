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
package rsb.transport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atomic uint32 implementation respecting
 * size of ProtocolBuffer uint32 type.
 * 
 * @author swrede
 *
 */
public class SequenceNumber {
	
	AtomicInteger count = new AtomicInteger();
	
	// uint32 max: 4.294.967.295
	public final static long MAX_VALUE = (long) 2*Integer.MAX_VALUE+1;  
	
	public SequenceNumber() {
	}	
	
	public SequenceNumber(long value) {
		if (Math.abs(value) > (SequenceNumber.MAX_VALUE)) {
			throw new IllegalArgumentException("Value larger than uint32");
		}
		count = new AtomicInteger((int) value);
	}

	public synchronized long incrementAndGet() {
		// respect uint32 size
		long inc = get()+1;
		if (inc > SequenceNumber.MAX_VALUE) {
			// reset to zero
			count = new AtomicInteger();
		} else {
			count.incrementAndGet();
		}
		return (long) count.get() & 0xffffffffL;
	}
	
	public long get() {
		return (long) count.get() & 0xffffffffL;
	}
	
}
