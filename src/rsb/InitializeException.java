/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010, 2011 CoR-Lab, Bielefeld University
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
 * An InitializeException indicates erroneous situations during
 * the setup of the communication infrastructure.
 *
 * Other sources for this exception are related to the spread group
 * communication framework. The value of getMessage() can be inspected
 * to determine the actual reason for this exception.
 *
 * @author swrede
 *
 */
public class InitializeException extends RSBException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5223250815059688771L;

	/**
	 *
	 */
	public InitializeException() {
	}

	/**
	 * @param message
	 */
	public InitializeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InitializeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializeException(String message, Throwable cause) {
		super(message, cause);
	}

}
