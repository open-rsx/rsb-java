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

import rsb.transport.AbstractConverter;

/**
 * @author swrede
 *
 */
public class StringConverter extends AbstractConverter {

	/* (non-Javadoc)
	 * @see rsb.transport.AbstractConverter#deserialize(java.lang.String, java.nio.ByteBuffer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(String typeinfo, ByteBuffer b) {
		// TODO think about additional checks on T
		if (typeinfo.equals("string")) {
			String s = new String(b.array());
			return (T) s;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see rsb.transport.AbstractConverter#serialize(java.lang.String, java.lang.Object)
	 */
	@Override
	public ByteBuffer serialize(String typeinfo, Object obj) {
		if (typeinfo.equals("string")) {
			String s = (String) obj;
			ByteBuffer b = ByteBuffer.wrap(s.getBytes());
			return b;
		}
		return null;
	}

}
