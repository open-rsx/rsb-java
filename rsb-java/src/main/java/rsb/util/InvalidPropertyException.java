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
package rsb.util;

/**
 * Thrown in case a desired property does not exist.
 */
public class InvalidPropertyException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5696967985100081449L;

    /**
     * Constructor without message and cause.
     */
    public InvalidPropertyException() {
        super();
    }

    /**
     * Constructor with explanatory message.
     *
     * @param message
     *            the message
     */
    public InvalidPropertyException(final String message) {
        super(message);
    }

    /**
     * Constructor with explanatory message and cause.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public InvalidPropertyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with only cause.
     *
     * @param cause
     *            the cause
     */
    public InvalidPropertyException(final Throwable cause) {
        super(cause);
    }

}
