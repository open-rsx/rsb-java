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

import java.util.HashSet;
import java.util.Set;

import rsb.filter.Filter;

/**
 * A decorator for {@link Handler}s that allows additional filtering for each
 * handler. Filters are a conjunction, hence one failure prevents delivery.
 * 
 * @author jwienke
 */
public class FilteringHandler implements Handler {

	private Handler decorated;
	private Set<Filter> filters = new HashSet<Filter>();

	/**
	 * Creates a filtering handler that wraps another handler and takes one
	 * additional filter.
	 * 
	 * @param decorated
	 *            handler to wrap and call if filter matches
	 * @param filter
	 *            the additional filter to apply
	 */
	public FilteringHandler(Handler decorated, Filter filter) {
		this.decorated = decorated;
		this.filters.add(filter);
	}

	/**
	 * Creates a filtering handler that wraps another handler and takes a set of
	 * filters to apply.
	 * 
	 * @param decorated
	 *            handler to wrap and call if filter matches
	 * @param filters
	 *            the additional filters to apply
	 */
	public FilteringHandler(Handler decorated, Set<Filter> filters) {
		this.decorated = decorated;
		this.filters.addAll(filters);
	}

	@Override
	public void internalNotify(Event e) {
		for (Filter f : filters) {
			if (f.transform(e) == null) {
				return;
			}
		}
		decorated.internalNotify(e);
	}

}
