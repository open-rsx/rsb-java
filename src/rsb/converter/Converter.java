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
package rsb.converter;


/**
 * This class represents a converter interface for a wire format T.
 * Implementations may support one or more domain types for (de-)serialization
 * to T and back to a specific object type referenced through the typeinfo
 * parameter.
 * 
 * @author swrede
 * @param <WireType>
 *            the wire format to serialize on
 */
public interface Converter<WireType> {

	/**
	 * Serializes user data to a wire representation.
	 * 
	 * @param typeInfo
	 *            programming language specific string describing the data to
	 *            serialize
	 * @param obj
	 *            data to serialize
	 * @return serialized data and generated wire schema
	 * @throws ConversionException
	 *             error converting the data
	 */
	public WireContents<WireType> serialize(String typeInfo, Object obj)
			throws ConversionException;

	/**
	 * Deserializes the data from the wire.
	 * 
	 * @param wireSchema
	 *            wire schema of the serialized data
	 * @param buffer
	 *            serialized data
	 * @return deserialized data
	 * @throws ConversionException
	 *             error deserializing from the wire
	 */
	@SuppressWarnings("rawtypes")
	public UserData deserialize(String wireSchema, WireType buffer)
			throws ConversionException;

	/**
	 * Get signature for this converter.
	 * 
	 * @return the @See ConverterSignature of this converter instance
	 *  
	 */
	public ConverterSignature getSignature();
	
}
