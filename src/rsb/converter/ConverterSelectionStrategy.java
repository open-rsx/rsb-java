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
 * Implementation of this interface perform mappings of one of the
 * followings forms:
 * - wire-schema -> @ref Converter
 * - data-type -> @ref Converter
 * 	
 * @author jmoringe
 * @author swrede
 *
 */
public interface ConverterSelectionStrategy<WireType> {
	
	/**
	 * Tries to look up the converter designated by @a key.
	 * 
	 * @param key
	 * @return
	 * @throws NoSuchConverterException
	 */
	Converter<WireType> getConverter(String key) throws NoSuchConverterException;

}
