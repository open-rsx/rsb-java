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

@SuppressWarnings("deprecation")
public class DefaultErrorHandler implements ErrorHandler {

	Logger log = Logger.getLogger(DefaultErrorHandler.class.getName());

	public DefaultErrorHandler(Logger l) {
		log = l;
	}

	public void error(RSBException ex) {
		// TODO unwrap root cause exception
		log.severe("An error was reported to the ErrorHandler: "
				+ ex.getMessage());
	}

	public void warning(RSBException ex) {
		// TODO unwrap root cause exception
		log.severe("A warning was reported to the ErrorHandler: "
				+ ex.getMessage());
	}

}
