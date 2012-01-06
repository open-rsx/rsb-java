/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
 * This execption indicates that a method is called on an object
 * that is not in the correct state to perform the requested service.
 * For instance, it is not possible to create object with the XcfManager
 * if it is not correctly activated.
 * 
 * @author swrede
 *
 */
public class InvalidStateException extends RuntimeException {

	public InvalidStateException(String message) {
		super(message);
	}

	public InvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidStateException(Throwable cause) {
		super(cause);
	}	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2396672331593990574L;

}
