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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;
import rsb.patterns.RemoteMethod.FuturePreparator;

/**
 * Objects of this class represent remote servers in a way that allows calling
 * methods on them as if they were local.
 *
 * @author jmoringe
 * @author swrede
 * @author jwienke
 */
public class RemoteServer extends Server<RemoteMethod> {

    private static final Logger LOG = Logger.getLogger(RemoteServer.class
            .getName());

    /**
     * Default timeout used to wait for method replies before throwing an
     * exception [sec].
     */
    public static final double DEFAULT_TIMEOUT = 25.0;

    private final double timeout;

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
     * @param config
     *            participant config to use
     */
    public RemoteServer(final Scope scope, final double timeout,
            final ParticipantConfig config) {
        super(scope, config);
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
     * @param config
     *            participant config to use
     */
    public RemoteServer(final String scope, final double timeout,
            final ParticipantConfig config) {
        super(scope, config);
        this.timeout = timeout;
    }

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     * @param config
     *            participant config to use
     */
    public RemoteServer(final Scope scope, final ParticipantConfig config) {
        super(scope, config);
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Create a new RemoteServer object that provides its methods under the
     * scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of the remote
     *            created server are provided.
     * @param config
     *            participant config to use
     */
    public RemoteServer(final String scope, final ParticipantConfig config) {
        super(scope, config);
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Returns the timeout used when waiting for replies from a server.
     *
     * @return timeout in seconds
     */
    public double getTimeout() {
        return this.timeout;
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
        final Future<Event> future = new Future<Event>();
        final EventFuturePreparator futurePreparator = new EventFuturePreparator(
                future);
        this.callAsyncEvent(name, event, futurePreparator);
        return future;
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
    public Future<Event> callAsync(final String name) throws RSBException {
        final Event event = new Event();
        event.setData(null);
        event.setType(Void.class);
        return this.callAsync(name, new Event(Void.class, null));
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
        final Future<ReplyType> future = new Future<ReplyType>();
        final DataFuturePreparator<ReplyType> resultPreparator = new DataFuturePreparator<ReplyType>(
                future);
        this.callAsyncData(name, data, resultPreparator);
        return future;
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
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public Event call(final String name, final Event event)
            throws RSBException, ExecutionException, TimeoutException {
        return this.call(name, event, this.getTimeout());
    }

    /**
     * Calls a method of the server using the method name and request data
     * encapsulated in an {@link Event} instance. The method blocks until the
     * server replied or until the specified timeout is reached.
     *
     * @param name
     *            name of the method to call
     * @param event
     *            request data
     * @param timeout
     *            seconds to wait for the reply
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public Event call(final String name, final Event event, final double timeout)
            throws RSBException, ExecutionException, TimeoutException {
        final Future<Event> future = new Future<Event>();
        final EventFuturePreparator futurePreparator = new EventFuturePreparator(
                future);
        this.callAsyncEvent(name, event, futurePreparator);
        return future
                .get((long) (timeout * 1000000000.0), TimeUnit.NANOSECONDS);
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
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public Event call(final String name) throws RSBException,
            ExecutionException, TimeoutException {
        return this.call(name, new Event(Void.class, null));
    }

    /**
     * Calls a method of the server without request parameter using the method
     * name. The method blocks until the server replied or until the specified
     * timeout is reached.
     *
     * @param name
     *            name of the method to call
     * @param timeout
     *            seconds to wait for the reply
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public Event call(final String name, final double timeout)
            throws RSBException, ExecutionException, TimeoutException {
        return this.call(name, new Event(Void.class, null), timeout);
    }

    /**
     * Calls a method of the server using the method name and plain request
     * data. The method blocks until the server replied or until the timeout is
     * reached.
     *
     * @param name
     *            name of the method to call
     * @param data
     *            request data
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public <ReplyType, RequestType> ReplyType call(final String name,
            final RequestType data) throws RSBException, ExecutionException,
            TimeoutException {
        return this.call(name, data, this.getTimeout());
    }

    /**
     * Calls a method of the server using the method name and plain request
     * data. The method blocks until the server replied or until the specified
     * timeout is reached.
     *
     * @param name
     *            name of the method to call
     * @param data
     *            request data
     * @param timeout
     *            seconds to wait for the reply
     * @return An event with the resulting data
     * @throws RSBException
     *             communication errors or server-side errors
     * @throws TimeoutException
     *             timeout waiting for the reply
     * @throws ExecutionException
     *             in case the method failed on the server side
     * @throws CancellationException
     *             waiting for the result was cancelled
     */
    public <ReplyType, RequestType> ReplyType call(final String name,
            final RequestType data, final double timeout) throws RSBException,
            ExecutionException, TimeoutException {
        final Future<ReplyType> future = new Future<ReplyType>();
        final DataFuturePreparator<ReplyType> resultPreparator = new DataFuturePreparator<ReplyType>(
                future);
        this.callAsyncData(name, data, resultPreparator);
        return future
                .get((long) (timeout * 1000000000.0), TimeUnit.NANOSECONDS);
    }

    private class EventFuturePreparator extends FuturePreparator<Event> {

        public EventFuturePreparator(final Future<Event> future) {
            super(future);
        }

        @Override
        public void result(final Event resultEvent) {
            final Future<Event> future = this.getFuture();
            if (future != null) {
                future.complete(resultEvent);
            }
        }

    }

    private class DataFuturePreparator<DataType> extends
            FuturePreparator<DataType> {

        public DataFuturePreparator(final Future<DataType> future) {
            super(future);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void result(final Event resultEvent) {
            final Future<DataType> future = this.getFuture();
            if (future != null) {
                future.complete((DataType) resultEvent.getData());
            }
        }

    }

    private void callAsyncEvent(final String name, final Event request,
            final FuturePreparator<?> resultPreparator) throws RSBException {
        RemoteMethod method = null;
        // get method, either new or cached
        synchronized (this) {
            if (hasMethod(name)) {
                method = getMethod(name);
                assert method != null;
            } else {
                try {
                    method = this.addMethod(name);
                } catch (final InitializeException exception) {
                    LOG.warning("Exception during method activation: "
                            + exception.getMessage() + " Re-throwing it.");
                    throw new RSBException(exception);
                } catch (final InterruptedException e) {
                    throw new RSBException(e);
                }
            }
        }
        method.call(request, resultPreparator);
    }

    private <RequestType> void callAsyncData(final String name,
            final RequestType requestData,
            final FuturePreparator<?> resultPreparator) throws RSBException {
        final Event request = new Event();
        // null needs to be specifically handled
        if (requestData == null) {
            request.setType(Void.class);
        } else {
            request.setType(requestData.getClass());
        }
        request.setData(requestData);
        this.callAsyncEvent(name, request, resultPreparator);
    }

    protected RemoteMethod addMethod(final String name) throws RSBException,
            InterruptedException {
        LOG.fine("Registering new method " + name);

        final RemoteMethod method = new RemoteMethod(this, name, getConfig());
        // it should never be possible that an exception is thrown for a
        // duplicated method because we take care of this
        addMethod(name, method, false);

        if (this.isActive()) {
            method.activate();
        }

        return method;
    }

};
