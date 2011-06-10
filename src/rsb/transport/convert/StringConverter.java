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
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import rsb.transport.ConversionException;
import rsb.transport.Converter;

/**
 * A converter with wire type {@link ByteBuffer} that is capable of handling
 * strings with different encodings.
 * 
 * @author swrede
 * @author jwienke
 */
public class StringConverter implements Converter<ByteBuffer> {

	private String wireSchema;
	private ThreadLocal<CharsetEncoder> encoder;
	private ThreadLocal<CharsetDecoder> decoder;

	/**
	 * Creates a converter for UTF-8 encoding with utf-8-string wire schema.
	 */
	public StringConverter() {
		this("UTF-8", "utf-8-string");
	}

	/**
	 * Creates a converter that uses the specified encoding for strings.
	 * 
	 * @param encoding
	 *            encoding name for the data
	 * @param wireSchema
	 *            wire schema of the serialized data
	 * @throws IllegalArgumentException
	 *             invalid encoding name
	 */
	public StringConverter(final String encoding, String wireSchema) {

		if (!Charset.isSupported(encoding)) {
			throw new IllegalArgumentException("Encoding '" + encoding
					+ "' is not supported.");
		}

		init(Charset.forName(encoding), wireSchema);

	}

	/**
	 * Creates a converter that uses the specified charset for strings.
	 * 
	 * @param charset
	 *            encoding for the data
	 * @param wireSchema
	 *            wire schema of the serialized data
	 */
	public StringConverter(Charset charset, String wireSchema) {
		init(charset, wireSchema);
	}

	private void init(final Charset charset, String wireSchema) {

		this.wireSchema = wireSchema;

		encoder = new ThreadLocal<CharsetEncoder>() {

			@Override
			protected CharsetEncoder initialValue() {
				CharsetEncoder encoder = charset.newEncoder();
				encoder.onMalformedInput(CodingErrorAction.REPORT);
				encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
				return encoder;
			}

		};
		decoder = new ThreadLocal<CharsetDecoder>() {

			@Override
			protected CharsetDecoder initialValue() {
				CharsetDecoder decoder = charset.newDecoder();
				decoder.onMalformedInput(CodingErrorAction.REPORT);
				decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
				return decoder;
			}

		};

	}

	@Override
	public WireContents<ByteBuffer> serialize(String typeInfo, Object data)
			throws ConversionException {

		try {

			String string = (String) data;

			ByteBuffer serialized = encoder.get().encode(
					CharBuffer.wrap(string));
			return new WireContents<ByteBuffer>(serialized, wireSchema);

		} catch (ClassCastException e) {
			throw new ConversionException(
					"Input data for serializing must be strings.", e);
		} catch (CharacterCodingException e) {
			throw new ConversionException(
					"Error serializing input data because of a charset problem.",
					e);
		}

	}

	@Override
	public UserData deserialize(String wireSchema, ByteBuffer bytes)
			throws ConversionException {

		if (!wireSchema.equals(this.wireSchema)) {
			throw new ConversionException("Unexpected wire schema '"
					+ wireSchema + "', expected '" + this.wireSchema + "'.");
		}

		try {

			String string = decoder.get().decode(bytes).toString();
			return new UserData(string, "string");

		} catch (CharacterCodingException e) {
			throw new ConversionException(
					"Error deserializing wire data because of a charset problem.",
					e);
		}

	}
}
