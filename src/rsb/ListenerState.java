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

import rsb.naming.NotFoundException;

public abstract class ListenerState {

	protected static Logger log = Logger.getLogger(InformerState.class.getName());

	protected Listener s;

	protected ListenerState (Listener ctx) {
		s = ctx;
	}

	protected void activate() throws InitializeException, NotFoundException {
		log.warning("invalid state exception during activate call");
		throw new InvalidStateException("subscriber already activated");
	}

	protected void deactivate() {
		log.warning("invalid state exception during deactivate call");
		throw new InvalidStateException("subscriber already deactivated");
	}

}
