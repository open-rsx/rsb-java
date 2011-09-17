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
 * handling unsigned integers that fit into 64 bits.
 *
 * @author jmoringe
 */
public class Uint64Converter implements Converter<ByteBuffer> {

    private ConverterSignature signature;

    public Uint64Converter() {
	signature = new ConverterSignature("uint64", Long.class);
    }

    @Override
    public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object data)
	throws ConversionException {

	try {
	    long value = (Long) data;
	    byte[] backing = new byte[8];
	    for (int i = 0; i < 8; ++i) {
		backing[i] = (byte) ((value & (0xff << (i * 8))) >> (i * 8));
	    }
	    ByteBuffer serialized =  ByteBuffer.wrap(backing);
	    return new WireContents<ByteBuffer>(serialized, signature.getSchema());

	} catch (ClassCastException e) {
	    throw new ConversionException("Input data for serializing must be long values.", e);
	}
    }

    @Override
    public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer bytes)
	throws ConversionException {

	if (!wireSchema.equals(signature.getSchema())) {
	    throw new ConversionException("Unexpected wire schema '"
					  + wireSchema + "', expected '" + signature.getSchema() + "'.");
	}

	long result = 0;
	for (int i = 0; i < 8; ++i) {
	    long value = (long) bytes.get(i);
	    if (value < 0L) {
		value = 256L + value;
	    }
	    result |= (value << (i * 8));
	}
	return new UserData<ByteBuffer>(result, Long.class);
    }

    @Override
    public ConverterSignature getSignature() {
	return signature;
    }
}
