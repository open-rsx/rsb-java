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
package rsb;

/**
 * An interface for handlers that are interested in whole {@link Event}
 * instances.
 * 
 * @author swrede
 * @see Event
 */
public abstract class AbstractEventHandler implements Handler {

	public void internalNotify(final Event event) {
		handleEvent(event);
	};

	public abstract void handleEvent(Event event);

}
