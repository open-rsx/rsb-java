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
import rsb.converter.NullConverter;
import rsb.converter.WireContents;

/**
 * @author jmoringe
 */
public class NullConverterTest {

    @Test
    public void serialize() throws Throwable {
	NullConverter c = new NullConverter();
	WireContents<ByteBuffer> buf = c.serialize(null, null);
	assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
	NullConverter c = new NullConverter();
	WireContents<ByteBuffer> buf = c.serialize(null, null);
	assertNotNull(buf);
	Object o = c.deserialize(buf.getWireSchema(), buf.getSerialization()).getData();
	assertNull(o);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotNull() throws Throwable {
	NullConverter c = new NullConverter();
	c.serialize(null, new LinkedList<Integer>());
    }

}
