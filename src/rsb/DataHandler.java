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
 * A handler that receives the user payload of an event by extracting the data
 * and casting them to the specified type.
 * 
 * @author swrede
 * @param <V>
 *            the desired target data type of the user handler. The event
 *            payload will be casted to this type
 */
public abstract class DataHandler<V> implements Handler<Event> {

	@SuppressWarnings("unchecked")
	@Override
	public void internalNotify(Event e) {
		try {
			handleEvent((V) e.getData());
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public abstract void handleEvent(V d);

}
