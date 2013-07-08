/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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
 * Superclass of all RSB exceptions that may be used in handlers that catch all
 * RSB-related exceptions.
 * 
 * @author swrede
 */
public class RSBException extends Exception {

    private static final long serialVersionUID = -5223250815059688771L;

    /**
     * Constructs an exception with empty message and cause.
     */
    public RSBException() {
        super();
    }

    /**
     * Constructs an exception with an explanatory message.
     * 
     * @param message
     *            the message
     */
    public RSBException(final String message) {
        super(message);
    }

    /**
     * Constructs an exception with causing {@link Throwable}.
     * 
     * @param cause
     *            causing exception
     */
    public RSBException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an exception with explanatory message and causing exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            causing exception
     */
    public RSBException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
