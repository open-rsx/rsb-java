/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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
package rsb.patterns;

import rsb.Event;

/**
 * Implementations of this interface are used to provide the behavior of exposed
 * methods.
 *
 * @author jmoringe
 */
public abstract class EventCallback implements Callback {

    // Convenience for users to throw anything
    @Override
    @SuppressWarnings({"PMD.AvoidCatchingGenericException",
            "PMD.AvoidRethrowingException"})
    public Event internalInvoke(final Event request) throws UserCodeException,
            InterruptedException {
        try {
            return this.invoke(request);
        } catch (final InterruptedException e) {
            // ensure that interruption is handled explicitly
            throw e;
        } catch (final Exception e) {
            throw new UserCodeException(e);
        }
    }

    /**
     * This method is called to invoke the actual behavior of an exposed method.
     *
     * Implementing user code must not swallow interruption state. Instead, it
     * has to be passed to the outside world through an {@link
     * InterruptedException}.
     *
     * @param request
     *            The argument passed to the associated method by the remote
     *            caller.
     * @return A result that should be returned to the remote caller as the
     *         result of the calling the method.
     * @throws InterruptedException
     *             Indicates that the operation was interrupted.
     * @throws Exception
     *             Can throw anything.
     */
    // Convenience for users to throw anything
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public abstract Event invoke(Event request) throws Exception;

};
