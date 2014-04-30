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
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;
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
 */
class LocalMethod extends Method implements Handler {

    private static final Logger LOG = Logger.getLogger(LocalMethod.class
            .getName());

    private final Callback callback;

    /**
     * Create a new {@link LocalMethod} object that is exposed under the name @a
     * name by @a server.
     *
     * @param name
     *            The name of the method.
     * @param parentScope
     *            The scope of the parent participant, typically the LocalServer.
     * @param callback
     *            The callback implementing the user functionality of the method
     * @param config
     *            participant configuration to use for internal informers and
     *            listeners
     * @throws InterruptedException
     *             error while installing method
     * @throws InitializeException
     *             error initializing the method or one of the underlying
     *             participants
     */
    public LocalMethod(final String name, final Scope parentScope,
            final Callback callback, final ParticipantConfig config)
            throws InterruptedException, InitializeException {
        super(name,parentScope,config);
        this.callback = callback;
        this.setListener(getFactory().createListener(this.getScope(), config));
        this.getListener().addFilter(new MethodFilter("REQUEST"));
        this.getListener().addHandler(this, true);
        this.setInformer(getFactory().createInformer(this.getScope(), config));
    }

    @Override
    public void internalNotify(final Event request) {

        Event reply;
        try {
            reply = this.callback.internalInvoke(request);
        } catch (final Throwable exception) {
            LOG.log(Level.WARNING,
                    "Exception during method invocation in participant: {0}. "
                            + "Exception message: {1}", new Object[] {
                            this.getScope(), exception });
            final StringWriter exceptionWriter = new StringWriter();
            final PrintWriter exceptionPrinter =
                    new PrintWriter(exceptionWriter);
            exception.printStackTrace(exceptionPrinter);
            final String error =
                    exception.toString() + " Details: "
                            + exception.getMessage() + "\n"
                            + exceptionWriter.toString();
            // return error information
            reply = new Event(String.class, error);
            reply.getMetaData().setUserInfo("rsb:error?", "1");
        }

        reply.setScope(this.getScope());
        reply.setMethod("REPLY");
        reply.addCause(request.getId());

        // send reply via method informer
        try {
            this.getInformer().send(reply);
        } catch (final RSBException exception) {
            // TODO call local error handler
            LOG.log(Level.WARNING,
                    "Exception while sending reply in server: {0}."
                            + " Exception message: {1}", new Object[] {
                            this.getScope(), exception });
        }
    }

};
