/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author swrede
 *
 */
public class DataInCallback implements DataCallback { 

	public AtomicInteger counter = new AtomicInteger();	
	
@Override
public Object invoke(Object request) throws Throwable {
	counter.incrementAndGet();
	return null;
}

}
