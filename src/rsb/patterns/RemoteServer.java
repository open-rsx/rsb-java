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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

/**
 * Objects of this class represent remote servers in a way that allows calling
 * methods on them as if they were local.
 * 
 * @author jmoringe
 * @author swrede
 * @author jwienke
 */
public class RemoteServer extends Server {

    private static final Logger LOG = Logger.getLogger(RemoteServer.class
            .getName());

    /**
     * Default timeout used to wait for method replies before throwing an
     * exception [sec].
     */
    public static final double DEFAULT_TIMEOUT = 25.0;

    private double timeout;

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     * 
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     * @param timeout
     *            The amount of seconds methods calls should wait for their
     *            replies to arrive before failing.
     */
    public RemoteServer(final Scope scope, double timeout) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
        this.timeout = timeout;
    }

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     * 
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     * @param timeout
     *            The amount of seconds methods calls should wait for their
     *            replies to arrive before failing.
     */
    public RemoteServer(final String scope, double timeout) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
        this.timeout = timeout;
    }

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     * 
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     */
    public RemoteServer(final Scope scope) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     * 
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     */
    public RemoteServer(final String scope) {
        super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Returns the timeout used when waiting for replies from a server.
     * 
     * @return timeout in seconds
     */
    public double getTimeout() {
        return timeout;
    }

    /**
     * Calls a method of the server using the method name and request data
     * encapsulated in an {@link Event} instance. The method returns immediately
     * with a {@link Future} instance.
     * 
     * @param name
     *            name of the method to call
     * @param event
     *            request data
     * @return A {@link Future} instance to retrieve the result {@link Event}
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public Future<Event> callAsync(final String name, final Event event)
            throws RSBException {
        return callAsyncInternal(name, event, true);
    }

    /**
     * Calls a method of the server without request parameter using the method
     * name. The method returns immediately with a {@link Future} instance.
     * 
     * @param name
     *            name of the method to call
     * @return A {@link Future} instance to retrieve the result {@link Event}
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public Future<Event> callAsync(String name) throws RSBException {
        Event event = new Event();
        event.setData(null);
        event.setType(Void.class);
        return callAsyncInternal(name, event, true);
    }

    /**
     * Calls a method of the server using the method name and plain request
     * data. The method returns immediately with a {@link Future} instance.
     * 
     * @param name
     *            name of the method to call
     * @param data
     *            the data to transfer as the method's request parameter
     * @return A {@link Future} instance to retrieve the result data
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public <ReplyType, RequestType> Future<ReplyType> callAsync(
            final String name, final RequestType data) throws RSBException {
        return callAsyncInternal(name, data, false);
    }

    /**
     * Calls a method of the server using the method name and request data
     * encapsulated in an {@link Event} instance. The method blocks until the
     * server replied or until the timeout is reached.
     * 
     * @param name
     *            name of the method to call
     * @param event
     *            request data
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public Event call(final String name, final Event event) throws RSBException {
        Event result = callInternal(name, event, true);
        return result;
    }

    /**
     * Calls a method of the server without request parameter using the method
     * name. The method blocks until the server replied or until the timeout is
     * reached.
     * 
     * @param name
     *            name of the method to call
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public Event call(final String name) throws RSBException {
        Event event = new Event();
        event.setData(null);
        event.setType(Void.class);
        return this.call(name, event);
    }

    /**
     * Calls a method of the server using the method name and plain request
     * data. The method blocks until the server replied or until the timeout is
     * reached.
     * 
     * @param name
     *            name of the method to call
     * @param event
     *            request data
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     */
    public <ReplyType, RequestType> ReplyType call(final String name,
            final RequestType data) throws RSBException {
        return this.<ReplyType, RequestType> callInternal(name, data, false);
    }

    @SuppressWarnings("unchecked")
    private <T, U> Future<T> callAsyncInternal(final String name, final U data,
            boolean isEvent) throws RSBException {
        AbstractRemoteMethod<T, U> method = null;
        // get method, either new or cached
        if (!methods.containsKey(name)) {
            try {
                method = (AbstractRemoteMethod<T, U>) addMethod(name, isEvent);
            } catch (InitializeException exception) {
                LOG.warning("Exception during method activation: "
                        + exception.getMessage() + " Re-throwing it.");
                throw new RSBException(exception);
            }
        } else {
            try {
                method = (AbstractRemoteMethod<T, U>) methods.get(name);
            } catch (ClassCastException exception) {
                LOG.warning("Exception during method activation: "
                        + exception.getMessage() + " Re-throwing it.");
                throw new RSBException(exception);
            }
        }
        return method.call(data);
    }

    // internal methods are to prevent recursive calls from call(string, event)
    // to call(string, event), which
    // are bound to occur as the originally intended target (the template)
    // method is not called in that situation.
    private <U, T> U callInternal(final String name, final T data,
            boolean isEvent) throws RSBException {
        Future<U> future = callAsyncInternal(name, data, isEvent);
        U result;
        try {
            result = future.get((long) timeout, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            LOG.warning("Exception during remote call: "
                    + exception.getMessage() + "; Re-throwing it.");
            throw new RSBException(exception);
        } catch (TimeoutException exception) {
            LOG.warning("Timeout during remote call; Throwing exception.");
            throw new RSBException(exception);
        }
        return result;
    }

    protected <U, T> AbstractRemoteMethod<?, ?> addMethod(String name,
            boolean isEvent) throws InitializeException {
        LOG.fine("Registering new method " + name);
        AbstractRemoteMethod<?, ?> method;
        if (isEvent) {
            method = new RemoteEventMethod(this, name);
        } else {
            method = new RemoteDataMethod<T, U>(this, name);
        }
        methods.put(name, method);

        if (this.isActive()) {
            method.activate();
        }

        return method;
    }

};
