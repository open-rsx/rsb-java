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
package rsb.transport;

import java.nio.ByteBuffer;

/**
 * @author swrede
 *
 */
public abstract class AbstractConverter {
	
	// TODO think about handling different wire formats
	
	String typeinfo;
	
	public abstract <T extends Object> ByteBuffer serialize(String typeinfo, T obj);
	public abstract <T extends Object> T deserialize(String typeinfo, ByteBuffer b);
	
	String getTypeInfo() {
			return typeinfo;
	}

}
