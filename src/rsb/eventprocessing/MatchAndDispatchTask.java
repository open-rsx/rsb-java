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

import java.util.concurrent.Callable;
import java.util.Map;
import java.util.Set;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * @author swrede
 */
public class MatchAndDispatchTask implements Callable<Boolean> {

	private Handler handler;
	private Set<Filter> filters;
	private Event event;
	private Map<Handler, Set<MatchAndDispatchTask>> handlerTasks;

	MatchAndDispatchTask(Handler handler, Set<Filter> filters, Event event,
			Map<Handler, Set<MatchAndDispatchTask>> handlerTasks) {
		this.handler = handler;
		this.filters = filters;
		this.event = event;
		this.handlerTasks = handlerTasks;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			if (match(event)) {
				try {
					handler.internalNotify(event);
				} catch (Throwable ex) {
					// TODO add logger, re-throw exception to user-specified
					// exception handler
					ex.printStackTrace();
				}
				return true;
			}
			return false;
		} finally {
			synchronized (handlerTasks) {
				handlerTasks.get(handler).remove(this);
			}
			handlerTasks.notifyAll();
		}
	}

	public boolean match(Event event) {
		Event result = event;
		synchronized (filters) {
			for (Filter filter : filters) {
				result = filter.transform(result);
				if (result == null) {
					return false;
				}
			}
		}
		return true;
	}

}
