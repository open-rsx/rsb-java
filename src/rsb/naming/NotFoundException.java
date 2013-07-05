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
package rsb.naming;

import rsb.RSBException;

/**
 * This exception type represents any case where a symbolic name was could not
 * be resolved to an RSB resource. For instance, if a RemoteServer object is to
 * be created using a server name that is not existing, a NameNotFoundException
 * is thrown.
 * 
 * @author swrede
 */
public class NotFoundException extends RSBException {

    /**
     */
    private static final long serialVersionUID = -8852918216048184478L;

    /**
     */
    public NotFoundException() {
    }

    /**
     * @param message
     */
    public NotFoundException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public NotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
