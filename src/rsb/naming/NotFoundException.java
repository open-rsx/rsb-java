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
package rsb.naming;

import rsb.RSBException;

/**
 * This exception type represents any case where a symbolic name
 * was could not be resolved to an RSB resource. For instance,
 * if a RemoteServer object is to be created using a server name that
 * is not existing, a NameNotFoundException is thrown.
 * 
 * @author swrede
 *
 */
public class NotFoundException extends RSBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8852918216048184478L;

	/**
	 * 
	 */
	public NotFoundException() {
	}

	/**
	 * @param message
	 */
	public NotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
