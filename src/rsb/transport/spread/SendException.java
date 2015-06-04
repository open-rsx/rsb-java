/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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
package rsb.transport.spread;

/**
 * Exception indicating an error while sending something via a spread
 * connection.
 *
 * @author jwienke
 */
public class SendException extends RuntimeException {

    private static final long serialVersionUID = -2915475969771402031L;

    /**
     * Constructor.
     *
     * @param message
     *            exception message
     */
    public SendException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            root cause
     */
    public SendException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            exception message
     * @param cause
     *            root cause
     */
    public SendException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
