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
package rsb.introspection;

/**
 * Indicates that required information from the OS cannot be retrieved.
 *
 * @author jwienke
 */
public class LacksOsInformationException extends RuntimeException {

    private static final long serialVersionUID = 3639257831624012965L;

    /**
     * Constructor.
     *
     * @param message
     *            reason
     * @param cause
     *            cause
     */
    public LacksOsInformationException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            reason
     */
    public LacksOsInformationException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            reason
     */
    public LacksOsInformationException(final Throwable cause) {
        super(cause);
    }

}
