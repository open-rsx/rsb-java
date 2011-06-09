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
package rsb.eventprocessing;

import rsb.Handler;
import rsb.filter.Filter;
import rsb.transport.EventHandler;

/**
 * An interface for strategies that dispatch received events to {@link Handler}
 * s.
 * 
 * @author jwienke
 */
public interface EventReceivingStrategy extends EventHandler {

	/**
	 * Adds a filter that is applied for all registered handlers some time after
	 * this method call.
	 * 
	 * @param filter
	 *            filter to add, not <code>null</code>
	 */
	void addFilter(Filter filter);

	/**
	 * Removes a filter that is remove for all registered handlers some time
	 * after this method call.
	 * 
	 * @param filter
	 *            filter to remove, not <code>null</code>
	 */
	void removeFilter(Filter filter);

	void addHandler(Handler handler, boolean wait);

	void removeHandler(Handler handler, boolean wait)
			throws InterruptedException;

	void shutdownAndWait() throws InterruptedException;

}
