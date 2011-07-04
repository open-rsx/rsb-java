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
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;

import org.junit.Test;

import rsb.converter.ConversionException;
import rsb.converter.StringConverter;
import rsb.converter.Converter.WireContents;

/**
 * @author swrede
 */
public class StringConverterTest {

	@Test
	public void serialize() throws Throwable {
		StringConverter c = new StringConverter();
		String s = "testcase";
		WireContents<ByteBuffer> buf = c.serialize("String", s);
		assertNotNull(buf);
	}

	@Test
	public void roundtrip() throws Throwable {
		StringConverter c = new StringConverter();
		String s1 = "testcase";
		WireContents<ByteBuffer> buf = c.serialize("String", s1);
		assertNotNull(buf);
		Object o = c.deserialize(buf.getWireSchema(), buf.getSerialization())
				.getData();
		String s2 = (String) o;
		assertEquals(s1, s2);
	}

	@Test(expected = ConversionException.class)
	public void serializationNotAStringError() throws Throwable {
		StringConverter c = new StringConverter();
		c.serialize("String", new LinkedList<Integer>());
	}

	@Test(expected = ConversionException.class)
	public void serializationEncodingError() throws Throwable {
		String withNonAscii = "đħħ←ŋæ¶æŧđ";
		StringConverter c = new StringConverter("US-ASCII", "ascii-string");
		c.serialize("String", withNonAscii);
	}

	@Test(expected = ConversionException.class)
	public void deserializationEncodingError() throws Throwable {
		String withNonAscii = "đħħ←ŋæ¶æŧđ";
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(withNonAscii));
		StringConverter c = new StringConverter("US-ASCII", "ascii-string");
		c.deserialize("ascii-string", buffer);
	}

}
