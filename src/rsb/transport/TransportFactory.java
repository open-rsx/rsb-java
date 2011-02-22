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

import rsb.transport.spread.SpreadFactory;


/**
 *
 * @author swrede
 */
public abstract class TransportFactory {

    public static TransportFactory getInstance() {
        return new SpreadFactory();
    };

    public Port createPort() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
