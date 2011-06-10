/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.transport;

import rsb.RSBException;

/**
 * An exception that indicates a conversion error in a {@link Converter}.
 * 
 * @author jwienke
 */
public class ConversionException extends RSBException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 2803718845375274143L;

	/**
	 * Constructor with a message.
	 * 
	 * @param message
	 *            error message
	 */
	public ConversionException(String message) {
		super(message);
	}

	/**
	 * Constructor with a root cause.
	 * 
	 * @param cause
	 *            root cause
	 */
	public ConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor with message and root cause.
	 * 
	 * @param message
	 *            error message
	 * @param cause
	 *            root cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
