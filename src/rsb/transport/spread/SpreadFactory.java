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

import rsb.transport.Port;
import rsb.transport.TransportFactory;
import rsb.transport.convert.ByteBufferConverter;

/**
 *
 * @author swrede
 */
public class SpreadFactory extends TransportFactory {

	private SpreadWrapper sw = null;
	
	SpreadWrapper getSpreadWrapper() {
		if (sw == null) {
			synchronized(SpreadFactory.class) {
				sw = new SpreadWrapper();
			}
		}
		return sw;
	}
	
	@Override
	public Port createPort() {
		// TODO check port multiplicity
		SpreadPort sp = new SpreadPort(new SpreadWrapper());		
		sp.addConverter("string", new ByteBufferConverter());
		return sp;
	}
	
}
