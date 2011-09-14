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

/**
 * @author swrede
 *
 */
public class DefaultConverters {

	/*
	 * Convenience method to register
	 * default converters for default
	 * wire type.
	 */
	public static void register() {
		// TODO add missing converters for default types
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new StringConverter());
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new StringConverter("US-ASCII", "ascii-string"));
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new Uint64Converter());
	}

}
