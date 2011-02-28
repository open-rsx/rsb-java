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
package rsb.transport.convert;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import rsb.util.Holder;

/**
 * @author swrede
 *
 */
public class ByteBufferConverterTest {

	/**
	 * Test method for {@link rsb.transport.convert.ByteBufferConverter#serialize(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testSerialize() {
		ByteBufferConverter c = new ByteBufferConverter();
		String s = "testcase";
		Holder<ByteBuffer> buf = c.serialize("string", s);
		assertNotNull(buf);
	}

	/**
	 * Test method for {@link rsb.transport.convert.ByteBufferConverter#deserialize(java.lang.String, rsb.util.Holder)}.
	 */
	@Test
	public void testDeserialize() {
		ByteBufferConverter c = new ByteBufferConverter();
		String s1 = "testcase";
		Holder<ByteBuffer> buf = c.serialize("string", s1);
		assertNotNull(buf);		
		String s2 = (String) c.deserialize("string", buf.value).value;
		assertTrue(s1.equals(s2));		
	}	
	
}
