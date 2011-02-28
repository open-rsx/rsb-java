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
	
	Logger log = Logger.getLogger(FilterObservable.class.getName());

	List<FilterObserver> observers = new ArrayList<FilterObserver>();
	
	public void addObserver(FilterObserver f) {
		observers.add(f);
	}
	
	public void removeObserver(FilterObserver f) {
		observers.remove(f);
	}
	
	public void notifyObservers(AbstractFilter f, FilterAction a) {
		for (FilterObserver o : observers) {
			// perform double dispatch
			f.dispachToObserver(o, a);
		}
	}
	
	public void clearObservers() {
		observers.clear();
	}
}
