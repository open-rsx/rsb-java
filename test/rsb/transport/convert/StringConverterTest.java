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

import org.junit.Test;

import rsb.transport.convert.StringConverter;
import rsb.util.Holder;

/**
 * @author swrede
 *
 */
public class StringConverterTest {

	/**
	 * Test method for {@link rsb.transport.convert.StringConverter#serialize(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testSerialize() {
		StringConverter c = new StringConverter();
		String s = "testcase";
		Holder<String> buf = c.serialize("string", s);
		assertNotNull(buf);
	}

	/**
	 * Test method for {@link rsb.transport.convert.StringConverter#deserialize(java.lang.String, rsb.util.Holder)}.
	 */
	@Test
	public void testDeserialize() {
		StringConverter c = new StringConverter();
		String s1 = "testcase";
		Holder<String> buf = c.serialize("string", s1);
		assertNotNull(buf);
		Object o = c.deserialize("string", buf.value).value;
		String s2 = (String) o; 		
		assertTrue(s2.equals(s1));
	}

}
