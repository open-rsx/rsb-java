/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.converter;

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
