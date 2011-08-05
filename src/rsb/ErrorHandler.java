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

/**
 * @deprecated not yet designed
 */
public interface ErrorHandler {

	// public interface ConnectionErrorHandler extends ErrorHandler {
	// public void handleError(ConnectionLostError ex);
	// }
	//
	// public void connectionLost();
	// public void lostConnectionToMemoryServer();
	//

	public void error(RSBException exception);

	public void warning(RSBException exception);

}
