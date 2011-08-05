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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import rsb.EventId;

/**
 * @author swrede
 *
 */
public class UUIDToolsTest {

	/**
	 * Test method for {@link rsb.util.UUIDTools#getNameBasedUUID(java.util.UUID, java.lang.String)}.
	 */
	@Test
	public void testGetNameBasedUUID() {	
		// Testcase A (see https://code.cor-lab.org/projects/rsb/wiki/Events)
		// event id v5-uuid(D8FBFEF4-4EB0-4C89-9716-C425DED3C527, "00000000")
		// => 84F43861-433F-5253-AFBB-A613A5E04D71
		String idString = "D8FBFEF4-4EB0-4C89-9716-C425DED3C527";
		long seqNr = 0;
		UUID id = buildUUID(idString, seqNr);
		assertTrue(id.toString().equals("84F43861-433F-5253-AFBB-A613A5E04D71".toLowerCase()));
		
		// Testcase B (see https://code.cor-lab.org/projects/rsb/wiki/Events)
		idString = "BF948D47-618F-4B04-AAC5-0AB5A1A79267";
		seqNr = 378;
		id = buildUUID(idString, seqNr);		
		// v5-uuid(BF948D47-618F-4B04-AAC5-0AB5A1A79267, "0000017a")
		// => BD27BE7D-87DE-5336-BECA-44FC60DE46A0		
		assertTrue(id.toString().equals("BD27BE7D-87DE-5336-BECA-44FC60DE46A0".toLowerCase()));
	}

	private UUID buildUUID(String idString, long seqNr) {
		UUID id = UUID.fromString(idString);
		// java UUId's are formatted lower case...
		assertTrue(id.toString().equalsIgnoreCase(idString));

		String seqNumber = EventId.formatSequenceNumber(seqNr);
		// hack for testcases A and B, should go to EventId test
		if (seqNr==0) {
			assertTrue(seqNumber.equals("00000000"));
		}
		if (seqNr==378) {
			assertTrue(seqNumber.equals("0000017a"));
		}
		
		return UUIDTools.getNameBasedUUID(id,seqNumber);
	}

	/**
	 * Test method for {@link rsb.util.UUIDTools#fromByteArray(byte[])}.
	 */
	@Test
	public void testByteArrayConversion() {
		UUID id1 = UUID.randomUUID();
		byte[] buffer = UUIDTools.toByteArray(id1);
		assertEquals(16, buffer.length);
		UUID id2 = UUIDTools.fromByteArray(buffer);
		assertEquals(id1, id2);
	}

}
