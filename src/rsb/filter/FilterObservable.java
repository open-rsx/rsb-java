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
package rsb.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class FilterObservable {
	
	private final static Logger LOG = Logger.getLogger(FilterObservable.class.getName());

	List<FilterObserver> observers = new ArrayList<FilterObserver>();
	
	public void addObserver(final FilterObserver observer) {
		LOG.finest("Added observer" + observer);
		observers.add(observer);
	}
	
	public void removeObserver(final FilterObserver observer) {
		LOG.finest("Removed observer" + observer);
		observers.remove(observer);
	}
	
	public void notifyObservers(final Filter filter, final FilterAction action) {
		for (FilterObserver observer : observers) {
			// perform double dispatch
			filter.dispachToObserver(observer, action);
		}
	}
	
	public void clearObservers() {
		observers.clear();
	}
}
