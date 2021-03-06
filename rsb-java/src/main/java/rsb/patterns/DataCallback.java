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
 * Implement the user behavior for a {@link LocalServer} method with a simple
 * signature without events. This makes implementing callbacks simple, because
 * no RSB {@link Event} instances need to be used. However, this e.g. prevents
 * setting timestamps or further meta data of events. If you need such a
 * behavior, please use {@link EventCallback}.
 *
 * In case you either do not need a request paramter or do not return any data,
 * specify the respective java generics parameter as {@link Void}. You can then
 * safely return <code>null</code> if you have no result. In other cases,
 * <code>null</code> is explicitly not allowed. TODO check the null assumption
 *
 * @author jmoringe
 * @author jwienke
 * @param <ReplyType>
 *            The type of data produced as replies to the implemented
 *            functionality.
 * @param <RequestType>
 *            The type of data required as request parameter
 */
public abstract class DataCallback<ReplyType, RequestType> implements Callback {

    // Convenience for users to throw anything
    @Override
    @SuppressWarnings({"PMD.AvoidCatchingGenericException",
            "PMD.AvoidRethrowingException"})
    public Event internalInvoke(final Event request) throws UserCodeException,
            InterruptedException {
        try {
            @SuppressWarnings("unchecked")
            final ReplyType result =
                    this.invoke((RequestType) request.getData());
            // wrap return data in event instance
            Class<?> type;
            // null needs to be specifically handled
            if (result == null) {
                type = Void.class;
            } else {
                type = result.getClass();
            }
            return new Event(type, result);
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
    public abstract ReplyType invoke(RequestType request) throws Exception;

};
