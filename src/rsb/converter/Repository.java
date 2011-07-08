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

import java.util.Map;

/**
 *
 * Maintains a collection of converters for a specific wire format. Each
 * converter has a wire type describing the actual message that is written on
 * the wire and a data type that indicates which data it can serialize on the
 * wire.
 * 
 * @author swrede
 *
 */
public class Repository<WireType> {

	Map<ConverterSignature, Converter<WireType> > converterMap;
	
	/**
	 * This method queries the converter map for seralizable data types
	 * and returns an UnambiguousConverterMap for the chosen <WireType> 
	 * to the caller.
	 * 
	 * @return ConverterSelectionStrategy object for serialization
	 */
	ConverterSelectionStrategy<WireType> getConvertersForSerialization(){
		UnambiguousConverterMap<WireType> outStrategy = new UnambiguousConverterMap<WireType>();
		//outStrategy.addConverter("String", new StringConverter());
		// Query Map for types
		return outStrategy;
	}
	
	ConverterSelectionStrategy<WireType> getConvertersForDeserialization(){
		UnambiguousConverterMap<WireType> inStrategy = new UnambiguousConverterMap<WireType>();
		//inStrategy.addConverter("utf-8-string", new StringConverter());
		//inStrategy.addConverter("ascii-string", new StringConverter("US-ASCII", "ascii-string"));		
		return inStrategy;
	}
	
	public void addConverter(Converter<WireType> converter) {
		if (converterMap.containsKey(converter.getSignature())) {
			throw new IllegalArgumentException();
		}
		converterMap.put(converter.getSignature(), converter);
	}
	
}
