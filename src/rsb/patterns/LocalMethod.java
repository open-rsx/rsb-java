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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.RSBException;
import rsb.filter.MethodFilter;

/**
 * Objects of this class implement and make available methods of a local server.
 * 
 * The actual behavior of methods is implemented by invoking arbitrary
 * user-supplied functions.
 * 
 * @author jmoringe
 * @author swrede
 * @author jwienke
 * 
 * @param <T>
 *            return type of supplied handler method
 * @param <U>
 *            parameter object type of supplied handler method
 * 
 */
class LocalMethod extends Method implements Handler {

    private final static Logger LOG = Logger.getLogger(LocalMethod.class
            .getName());

    private Callback callback;

    /**
     * Create a new {@link LocalMethod} object that is exposed under the name @a
     * name by @a server.
     * 
     * @param server
     *            The local server object to which the method will be
     *            associated.
     * @param name
     *            The name of the method.
     * @param callback
     *            The callback implementing the user functionality of the method
     */
    public LocalMethod(final Server server, final String name,
            final Callback callback) {
        super(server, name);
        this.callback = callback;
        listener = factory.createListener(REQUEST_SCOPE);
        listener.addFilter(new MethodFilter("REQUEST"));
        listener.addHandler(this, true);
        informer = factory.createInformer(REPLY_SCOPE);
    }

    @Override
    public void internalNotify(Event request) {

        Event reply;
        try {
            reply = callback.internalInvoke(request);
        } catch (Throwable exception) {
            LOG.warning("Exception during method invocation in participant: "
                    + REQUEST_SCOPE + " Exception message: " + exception);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            final String error = exception.toString() + " Details: "
                    + exception.getMessage() + "\n" + sw.toString();
            // return error information
            reply = new Event();
            reply.setType(String.class);
            reply.getMetaData().setUserInfo("rsb:error?", "1");
            reply.setData(error);
        }

        reply.setScope(REPLY_SCOPE);
        reply.setMethod("REPLY");
        reply.addCause(request.getId());

        // send reply via method informer
        try {
            getInformer().send(reply);
        } catch (RSBException exception) {
            // TODO call local error handler
            LOG.severe("Exception while sending reply in server method: "
                    + REPLY_SCOPE + " Exception message: "
                    + exception.getMessage());
        }
    }

};
