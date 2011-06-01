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
package rsb.event;

import java.util.concurrent.Callable;
import java.util.List;

import rsb.Event;
import rsb.filter.Filter;

/**
 * @author swrede
 * 
 */
public class MatchAndDispatchTask implements Callable<Boolean> {

	Handler<Event> handler;
	List<Filter> filters;
	Event event;

	MatchAndDispatchTask(Handler<Event> handler, List<Filter> filters, Event event) {
		this.handler = handler;
		this.filters = filters;
		this.event = event;
	}

	@Override
	public Boolean call() throws Exception {
		if (match(event)) {
			try {
				handler.internalNotify(event);
			} catch (Exception ex) {
				// TODO add logger, re-throw exception to user-specified
				// exception handler
				ex.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public boolean match(Event event) {
		for (Filter filter : filters) {
			event = filter.transform(event);
			if (event == null)
				return false;
		}
		return true;
	}

}
