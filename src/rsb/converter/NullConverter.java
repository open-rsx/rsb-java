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

/**
 * A converter with wire type {@link ByteBuffer} that is capable of
 * handling the null value.
 *
 * @author jmoringe
 */
public class NullConverter implements Converter<ByteBuffer> {

    private ConverterSignature signature;

    public NullConverter() {
	signature = new ConverterSignature("void", null);
    }

    @Override
    public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object data)
	throws ConversionException {

	if (data != null) {
	    throw new ConversionException("The only acceptable value is null.");
	}

	byte[] backing = new byte[0];
	ByteBuffer serialized =  ByteBuffer.wrap(backing);
	return new WireContents<ByteBuffer>(serialized, signature.getSchema());
    }

    @Override
    public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer bytes)
	throws ConversionException {

	if (!wireSchema.equals(signature.getSchema())) {
	    throw new ConversionException("Unexpected wire schema '"
					  + wireSchema + "', expected '" + signature.getSchema() + "'.");
	}

	return new UserData<ByteBuffer>(null, null);
    }

    @Override
    public ConverterSignature getSignature() {
	return signature;
    }
}
