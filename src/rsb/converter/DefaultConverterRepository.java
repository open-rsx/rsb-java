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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class DefaultConverterRepository<WireType> implements ConverterRepository<WireType> {

	private final static Logger LOG = Logger.getLogger(DefaultConverterRepository.class.getName());
	
	private static ConverterRepository<ByteBuffer> defaultInstance = new DefaultConverterRepository<ByteBuffer>();
	
	private transient final Map<ConverterSignature, Converter<WireType> > converterMap = new HashMap<ConverterSignature, Converter<WireType> >();
	
	/**
	 * @return the converterMap
	 */
	protected Map<ConverterSignature, Converter<WireType>> getConverterMap() {
		return converterMap;
	}

	/* (non-Javadoc)
	 * @see rsb.converter.ConverterRepository#getConvertersForSerialization()
	 */
	@Override
	public ConverterSelectionStrategy<WireType> getConvertersForSerialization(){
		final UnambiguousConverterMap<WireType> outStrategy = new UnambiguousConverterMap<WireType>();
		// where to register the initial converters
		//outStrategy.addConverter("String", new StringConverter());
		// Query Map for types
		for (ConverterSignature s : converterMap.keySet()) {
			// put datatype and converter into unambiguous converter map
			if (s.getSchema().contentEquals("ascii-string")) {
				// adding two String representations would yield ambiguity
				// we want to use the UTF-8 representation for strings as default
				LOG.fine("skipping ascii-string converter for Serialization map");
			} else {
				// all other converters are added at this point
				outStrategy.addConverter(s.getDatatype().getName(), converterMap.get(s));
			}
		}
		return outStrategy;
	}
	
	/* (non-Javadoc)
	 * @see rsb.converter.ConverterRepository#getConvertersForDeserialization()
	 */
	@Override
	public ConverterSelectionStrategy<WireType> getConvertersForDeserialization(){
		final UnambiguousConverterMap<WireType> inStrategy = new UnambiguousConverterMap<WireType>();
		// Query Map for wire schemas
		for (ConverterSignature s : converterMap.keySet()) {
			// put datatype and converter into unambiguous converter map
			inStrategy.addConverter(s.getSchema(), converterMap.get(s));
		}		
		return inStrategy;
	}
	
	/* (non-Javadoc)
	 * @see rsb.converter.ConverterRepository#addConverter(rsb.converter.Converter)
	 */
	@Override
	public void addConverter(final Converter<WireType> converter) {
		if (converterMap.containsKey(converter.getSignature())) {
			LOG.warning("Converter with signature " + converter.getSignature() + " already registered in DefaultConverterRepository. Existing entry will be overwritten!");
		}
		converterMap.put(converter.getSignature(), converter);
	}
	
	public static ConverterRepository<ByteBuffer> getDefaultConverterRepository() {
		return defaultInstance;
	}
	
}
