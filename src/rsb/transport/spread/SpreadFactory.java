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
package rsb.transport.spread;

import java.nio.ByteBuffer;

import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;
import rsb.transport.EventHandler;
import rsb.transport.Port;
import rsb.transport.TransportFactory;

/**
 * 
 * @author swrede
 */
public class SpreadFactory extends TransportFactory {

	@Override
	public Port createPort(EventHandler handler) {
		UnambiguousConverterMap<ByteBuffer> inStrategy = new UnambiguousConverterMap<ByteBuffer>();
		inStrategy.addConverter("utf-8-string", new StringConverter());
		inStrategy.addConverter("ascii-string", new StringConverter("US-ASCII", "ascii-string"));

		UnambiguousConverterMap<ByteBuffer> outStrategy = new UnambiguousConverterMap<ByteBuffer>();
		outStrategy.addConverter("String", new StringConverter());

		SpreadPort sp = new SpreadPort(new SpreadWrapper(), handler, inStrategy, outStrategy);
		return sp;
	}

}
