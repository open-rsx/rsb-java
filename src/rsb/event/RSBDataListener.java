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

import rsb.RSBEvent;

/**
 * @author swrede
 * 
 */
public abstract class RSBDataListener<V> implements RSBListener<RSBEvent> {

	@SuppressWarnings("unchecked")
	@Override
	public void internalNotify(RSBEvent e) {
		try {
			handleEvent((V) e.getData());
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public abstract void handleEvent(V d);

}
