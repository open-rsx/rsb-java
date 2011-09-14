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

package rsb.converter;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import org.junit.Test;

import rsb.converter.ConversionException;
import rsb.converter.Uint64Converter;
import rsb.converter.WireContents;

/**
 * @author jmoringe
 */
public class Uint64ConverterTest {

    @Test
    public void serialize() throws Throwable {
	Uint64Converter c = new Uint64Converter();
	Long l = 2431709L;
	WireContents<ByteBuffer> buf = c.serialize(Long.class, l);
	assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
	Uint64Converter c = new Uint64Converter();
	Long l1 = 24398L;
	WireContents<ByteBuffer> buf = c.serialize(Long.class, l1);
	assertNotNull(buf);
	Object o = c.deserialize(buf.getWireSchema(), buf.getSerialization())
	    .getData();
	Long l2 = (Long) o;
	assertEquals(l1, l2);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotALongError() throws Throwable {
	Uint64Converter c = new Uint64Converter();
	c.serialize(Long.class, new LinkedList<Integer>());
    }

}
