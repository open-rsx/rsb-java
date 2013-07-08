/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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
 * An InitializeException indicates erroneous situations during the setup of the
 * communication infrastructure.
 * 
 * Other sources for this exception are related to the spread group
 * communication framework. The value of getMessage() can be inspected to
 * determine the actual reason for this exception.
 * 
 * @author swrede
 */
public class InitializeException extends RSBException {

    private static final long serialVersionUID = -5223250815059688771L;

    /**
     * Constructs a new exception with empty message and cause.
     */
    public InitializeException() {
        super();
    }

    /**
     * Constructs a new exception with an explaining message.
     * 
     * @param message
     *            the explanation
     */
    public InitializeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a {@link Throwable} as causing exception.
     * 
     * @param cause
     *            the causing exception
     */
    public InitializeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with explaining message and causing exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the causing exception
     */
    public InitializeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
