/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import rsb.Event;
import rsb.RSBException;

/**
 * A {@link Callback} implements the functionality of a single method of a
 * {@link LocalServer}.
 *
 * Clients usually should not implement this interface directly. Instead use one
 * of the specialized implementing classes like {@link DataCallback} or
 * {@link EventCallback}.
 *
 * @author jwienke
 */
public interface Callback {

    /**
     * Exception to be thrown by callbacks to indicate an error while processing
     * the user code. Exceptions of this type will always have a cause.
     *
     * @author jwienke
     */
    class UserCodeException extends RSBException {

        private static final long serialVersionUID = -1957188490022606548L;

        /**
         * Constructor.
         *
         * @param message
         *            Description of the problem
         * @param cause
         *            wrapped exception as the root cause, not <code>null</code>
         */
        public UserCodeException(final String message, final Exception cause) {
            super(message, cause);
            ensureCause(cause);
        }

        /**
         * Constructor.
         *
         * @param cause
         *            wrapped exception as the root cause, not <code>null</code>
         */
        public UserCodeException(final Exception cause) {
            super(cause);
            ensureCause(cause);
        }

        private static void ensureCause(final Exception cause) {
            if (cause == null) {
                throw new IllegalArgumentException(
                        "Causing exception must not be null");
            }
        }

    }

    /**
     * Method to invoke the {@link Callback}'s functionality. This method can be
     * considered an internal implementation detail.
     *
     * Implementing user code must not swallow interruption state. Instead, it
     * has to be passed to the outside world.
     *
     * @param request
     *            the request received from a {@link RemoteServer} method call
     * @return an event with the result data of a method
     * @throws UserCodeException
     *             any exception in the user code invoked by the callback
     * @throws InterruptedException
     *             indicates that the called action was interrupted
     */
    Event internalInvoke(Event request) throws UserCodeException,
             InterruptedException;

}
