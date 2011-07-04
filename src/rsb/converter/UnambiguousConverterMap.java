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

import java.util.HashMap;
import java.util.Map;

/**
 * @author swrede
 * @author jmoringe
 * @param <WireType>
 *
 */
public class UnambiguousConverterMap<WireType> implements
		ConverterSelectionStrategy<WireType> {

	Map<String,Converter<WireType> > converters = new HashMap<String,Converter<WireType> >();
	
	@Override
	public Converter<WireType> getConverter(String key)
			throws NoSuchConverterException {
		if (converters.containsKey(key)) {
			return converters.get(key);
		}
		throw new NoSuchConverterException("No converter with key " + key + " registered in ConverterMap");
	}	
	
	public void addConverter(String key, Converter<WireType> c) {
		if (converters.containsKey(key)) {
			throw new IllegalArgumentException();
		}
		converters.put(key, c);
	}

}
