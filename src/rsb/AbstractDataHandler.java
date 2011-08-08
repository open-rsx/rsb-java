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

import java.util.logging.Logger;

/**
 * A handler that receives the user payload of an event by extracting the data
 * and casting them to the specified type.
 * 
 * @author swrede
 * @param <V>
 *            the desired target data type of the user handler. The event
 *            payload will be casted to this type
 */
public abstract class AbstractDataHandler<V> implements Handler {

	private static final Logger LOG = Logger.getLogger(AbstractDataHandler.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void internalNotify(final Event event) {
		try {
			handleEvent((V) event.getData());
		} catch (RuntimeException ex) {
			LOG.warning("RuntimeException during event dispatching: " + ex.getMessage() + " Re-throwing it.");
			throw ex;
		}
	}

	public abstract void handleEvent(V data);

}
