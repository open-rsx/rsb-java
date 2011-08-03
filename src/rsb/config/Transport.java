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
package rsb.config;

import java.util.ArrayList;
import java.util.List;

import rsb.converter.ConverterSignature;

/**
 * @author swrede
 *
 */
public class Transport {

	public List<ConverterSignature> getConverters() {
		ArrayList<ConverterSignature> result = new ArrayList<ConverterSignature>();
		// TODO add string
		// this is for Spread transport only currently
		result.add(new ConverterSignature("utf-8-string", String.class));
		return result;		
	}
	
}
