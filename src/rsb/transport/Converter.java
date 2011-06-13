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
	 * A wrapper around the contents to be place on the wire.
	 * 
	 * @author jwienke
	 * @param <WireType>
	 *            serialization type of the wire contents
	 */
	public class WireContents<WireType> {

		private WireType serialization;
		private String wireSchema;

		/**
		 * Constructs a new wrapper around serialized data.
		 * 
		 * @param serialization
		 *            the serialized data
		 * @param wireSchema
		 *            the wire schema identifier of the serialized data
		 */
		public WireContents(WireType serialization, String wireSchema) {
			this.serialization = serialization;
			this.wireSchema = wireSchema;
		}

		/**
		 * Returns the contents for the wire in their serialized form.
		 * 
		 * @return serialized contents
		 */
		public WireType getSerialization() {
			return serialization;
		}

		/**
		 * Returns the identifier of the wire schema that was used to serialize
		 * the contents.
		 * 
		 * @return wire schema identifier
		 */
		public String getWireSchema() {
			return wireSchema;
		}

	}

	/**
	 * A wrapper around deserialized data that contains the unspecific
	 * {@link Object} instance with a string describing its type.
	 * 
	 * @author jwienke
	 */
	public class UserData {

		private Object data;
		private String typeInfo;

		public UserData(Object data, String typeInfo) {
			this.data = data;
			this.typeInfo = typeInfo;
		}

		/**
		 * Returns the deserialized data.
		 * 
		 * @return deserialized data
		 */
		public Object getData() {
			return data;
		}

		/**
		 * String describing the type of the deserialized data.
		 * 
		 * @return string type info
		 */
		public String getTypeInfo() {
			return typeInfo;
		}

	}

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
	public UserData deserialize(String wireSchema, WireType buffer)
			throws ConversionException;

}
