/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010, 2011 CoR-Lab, Bielefeld University
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
