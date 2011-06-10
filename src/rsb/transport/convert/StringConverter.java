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

import rsb.transport.Converter;
import rsb.util.Holder;

/**
 * @author swrede
 *
 */
public class StringConverter implements Converter<String> {

	// TODO throw exceptions instead returning null
	
	/**
	 * Converts Strings to String representations...
	 * 
	 * @param typeinfo name of type to be serialized
	 * @param s object object to be serialized to String encoding
	 */
	@Override
	public Holder<String> serialize(String typeinfo, Object s) {
		if (typeinfo.equals("string")) {
			return new Holder<String>((String) s);
		}
		return null;
	}

	/**
	 * Converts String representations to Strings...
	 * 
	 * @param typeinfo name of type to be deserialized
	 * @param s string representation of object to be deserialized
	 */
	@Override
	public Holder<Object> deserialize(String typeinfo, String s) {
		if (typeinfo.equals("string")) {			
			return new Holder<Object>(s);
		}
		return null;
	}	
	
}
