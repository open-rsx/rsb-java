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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author swrede
 *
 */
public class ConverterSignatureTest {

	/**
	 * Test method for {@link rsb.converter.ConverterSignature#ConverterSignature(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConverterSignature() {
		ConverterSignature sig = new ConverterSignature("utf-8-string", String.class);
		assertTrue(sig.getSchema().contentEquals("utf-8-string"));
		assertTrue(sig.getDatatype().getName().contentEquals("java.lang.String"));
	}

	/**
	 * Test method for {@link rsb.converter.ConverterSignature#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		ConverterSignature sig1 = new ConverterSignature("utf-8-string", String.class);
		ConverterSignature sig2 = new ConverterSignature("utf-8-string", String.class);
		assertEquals(sig1, sig2);
	}

}
