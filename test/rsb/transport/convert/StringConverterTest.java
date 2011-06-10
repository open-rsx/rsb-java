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

import rsb.transport.Converter.WireContents;
import rsb.transport.convert.StringConverter;

/**
 * @author swrede
 */
public class StringConverterTest {

	@Test
	public void serialize() throws Throwable {
		StringConverter c = new StringConverter();
		String s = "testcase";
		WireContents<ByteBuffer> buf = c.serialize("string", s);
		assertNotNull(buf);
	}

	@Test
	public void roundtrip() throws Throwable {
		StringConverter c = new StringConverter();
		String s1 = "testcase";
		WireContents<ByteBuffer> buf = c.serialize("string", s1);
		assertNotNull(buf);
		Object o = c.deserialize(buf.getWireSchema(), buf.getSerialization())
				.getData();
		String s2 = (String) o;
		assertTrue(s2.equals(s1));
	}

}
