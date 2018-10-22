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
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.InformerCreateArgs;
import rsb.InitializeException;
import rsb.ListenerCreateArgs;
import rsb.ParticipantCreateArgs;
import rsb.RSBException;
import rsb.filter.MethodFilter;
import rsb.patterns.Callback.UserCodeException;

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
     * @param args
     *            arguments used to create this method
     * @param callback
     *            The callback implementing the user functionality of the method
     * @throws InterruptedException
     *             error while installing method
     * @throws InitializeException
     *             error initializing the method or one of the underlying
     *             participants
     */
    public LocalMethod(final ParticipantCreateArgs<?> args,
            final Callback callback)
            throws InterruptedException, InitializeException {
        super(args);
        this.callback = callback;
        this.setListener(getFactory().createListener(
                new ListenerCreateArgs().setScope(this.getScope()).setConfig(
                        this.getConfig()).setParent(this)));
        this.getListener().addFilter(new MethodFilter("REQUEST"));
        this.setInformer(getFactory().createInformer(
                new InformerCreateArgs().setScope(this.getScope()).setConfig(
                        this.getConfig()).setParent(this)));
    }

    // We want clients to be able to throw anything so that good errors can be
    // sent to the caller
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void internalNotify(final Event request) {

        Event reply;
        try {
            reply = this.callback.internalInvoke(request);
            if (reply == null) {
                throw new UserCodeException(
                        "Received a null reply from the client code",
                        new IllegalArgumentException(
                            "Null reply from callback"));
            }
        } catch (final UserCodeException e) {
            final Throwable exception = e.getCause();
            LOG.log(Level.WARNING,
                    "Exception during method invocation in participant: {0}. "
                            + "Exception message: {1}",
                    new Object[] { this.getScope(), exception });
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
            this.getInformer().publish(reply);
        } catch (final RSBException exception) {
            // TODO call local error handler
            LOG.log(Level.WARNING,
                    "Exception while sending reply in server: {0}."
                            + " Exception message: {1}",
                    new Object[] { this.getScope(), exception });
        }
    }

    @Override
    public String getKind() {
        return "local-method";
    }

    @Override
    public Class<?> getDataType() {
        return null;
    }

    @Override
    public Set<URI> getTransportUris() {
        return new HashSet<URI>();
    }

    @Override
    public void activate() throws RSBException {
        // Performs all the necessary state checking
        super.activate();
        // Register the handler for receiving requests once all participants
        // have been set up by the base class to ensure that a request can
        // actually be answered by the active informer. Refer to #2580.
        try {
            this.getListener().addHandler(this, true);
        } catch (final InterruptedException e) {
            LOG.log(Level.WARNING, "Interrupted while registering the callback",
                    e);
            Thread.currentThread().interrupt();
            throw new RSBException(e);
        }
    }

};
