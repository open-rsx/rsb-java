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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import rsb.transport.Converter;
import rsb.util.Holder;

/**
 * @author swrede
 */
public class ByteBufferConverter implements Converter<ByteBuffer> {

	@Override
	public Holder<Object> deserialize(String typeinfo, ByteBuffer buffer) {
		if (typeinfo.equals("string")) {
			return new Holder<Object>(new String(buffer.array(),
					Charset.forName("US-ASCII")));
		}
		return null;
	}

	@Override
	public Holder<ByteBuffer> serialize(String typeinfo, Object s) {
		if (typeinfo.equals("string")) {
			ByteBuffer bb = ByteBuffer.wrap(((String) s).getBytes(Charset
					.forName("US-ASCII")));
			return new Holder<ByteBuffer>(bb);
		}
		return null;
	}

}
