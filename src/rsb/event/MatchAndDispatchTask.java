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

import rsb.RSBEvent;
import rsb.filter.Subscription;

/**
 * @author swrede
 *
 */
public class MatchAndDispatchTask implements Callable<Boolean> {

	RSBEvent e;
	Subscription s;
	
	MatchAndDispatchTask(Subscription s, RSBEvent e) {
		this.e = e;
	    this.s = s;
	}
	
	@Override
	public Boolean call() throws Exception {
		if (s.match(e)) {
			s.dispatch(e);
			return true;
		} 
		return false;
	}

}
