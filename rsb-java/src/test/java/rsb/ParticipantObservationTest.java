/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import rsb.ParticipantObservationTest.FactoryObserver.ObserverCall;
import rsb.ParticipantObservationTest.FactoryObserver.ObserverCall.CallType;
import rsb.config.ParticipantConfig;
import rsb.patterns.Callback;
import rsb.patterns.LocalServer;
import rsb.patterns.RemoteServer;

/**
 * Tests if {@link ParticipantObserver} are notified about participant creation
 * and destruction.
 *
 * @author swrede
 * @author jwienke
 * @author jmoringe
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ParticipantObservationTest extends LoggingEnabled {

    private static final Scope TEST_SCOPE = new Scope("/factorytest");

    private Factory factory;
    private ParticipantConfig config;

    @Before
    public void setUp() {
        this.factory = Factory.getInstance();
        this.config = this.factory.getDefaultParticipantConfig().copy();
        this.config.setIntrospectionEnabled(false);
    }

    public static class FactoryObserver implements ParticipantObserver {

        // CHECKSTYLE.OFF: VisibilityModifier - Acceptable for testing
        public List<ObserverCall> calls = new LinkedList<ObserverCall>();

        public static class ObserverCall {

            public CallType type;
            public Participant participant;

            public enum CallType {
                CREATE, DESTROY
            }

            @Override
            public String toString() {
                return "ObserverCall [type=" + this.type + ", participant="
                        + this.participant + "]";
            }

        }

        // CHECKSTYLE.ON: VisibilityModifier

        @Override
        public void created(final Participant participant,
                final ParticipantCreateArgs<?> args) {
            final ObserverCall call = new ObserverCall();
            call.type = CallType.CREATE;
            call.participant = participant;
            this.calls.add(call);
        }

        @Override
        public void destroyed(final Participant participant) {
            final ObserverCall call = new ObserverCall();
            call.type = CallType.DESTROY;
            call.participant = participant;
            this.calls.add(call);
        }

    }

    private void checkCall(final ObserverCall call,
            final CallType expectedType, final String expectedClassName,
            final Scope expectedScope) {
        assertEquals(expectedType, call.type);
        assertEquals(expectedClassName, call.participant.getClass().getName());
        assertEquals(expectedScope, call.participant.getScope());
    }

    private Scope scopeFromName(final String name) {
        return new Scope("/" + name);
    }

    @Test
    public void listenerNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);
        final Listener listener
            = this.factory.createListener(TEST_SCOPE, this.config);
        listener.activate();
        listener.deactivate();
        this.factory.removeObserver(observer);

        assertEquals(2, observer.calls.size());
        checkCall(observer.calls.get(0), CallType.CREATE,
                Listener.class.getName(), TEST_SCOPE);
        checkCall(observer.calls.get(1), CallType.DESTROY,
                Listener.class.getName(), TEST_SCOPE);

    }

    @Test
    public void informerNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);
        final Informer<String> informer
            = this.factory.createInformer(TEST_SCOPE, String.class, this.config);
        informer.activate();
        informer.deactivate();
        this.factory.removeObserver(observer);

        assertEquals(2, observer.calls.size());
        checkCall(observer.calls.get(0), CallType.CREATE,
                Informer.class.getName(), TEST_SCOPE);
        checkCall(observer.calls.get(1), CallType.DESTROY,
                Informer.class.getName(), TEST_SCOPE);

    }

    @Test
    public void remoteServerNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);
        final RemoteServer remote
            = this.factory.createRemoteServer(TEST_SCOPE, this.config);
        remote.activate();
        remote.deactivate();
        this.factory.removeObserver(observer);

        assertEquals(2, observer.calls.size());
        checkCall(observer.calls.get(0), CallType.CREATE,
                RemoteServer.class.getName(), TEST_SCOPE);
        checkCall(observer.calls.get(1), CallType.DESTROY,
                RemoteServer.class.getName(), TEST_SCOPE);

    }

    @Test
    public void localServerNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);
        final LocalServer server
            = this.factory.createLocalServer(TEST_SCOPE, this.config);
        server.activate();
        server.deactivate();
        this.factory.removeObserver(observer);

        assertEquals(2, observer.calls.size());
        checkCall(observer.calls.get(0), CallType.CREATE,
                LocalServer.class.getName(), TEST_SCOPE);
        checkCall(observer.calls.get(1), CallType.DESTROY,
                LocalServer.class.getName(), TEST_SCOPE);

    }

    private void checkMethodSequence(final List<ObserverCall> calls,
            final String methodClassName, final String methodName)
            throws Throwable {
        // CHECKSTYLE.OFF: MagicNumber - explanations given in the comments
        assertEquals("We must have been called twice for the Server, "
                + "the Method, and one Informer and Listener -> 8", 8,
                calls.size());
        // string names required due to visibility of the class
        checkCall(calls.get(3), CallType.CREATE, methodClassName,
                TEST_SCOPE.concat(scopeFromName(methodName)));
        checkCall(calls.get(5), CallType.DESTROY, methodClassName,
                TEST_SCOPE.concat(scopeFromName(methodName)));
        // CHECKSTYLE.ON: MagicNumber
    }

    // false positive due to asserts in called method
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void localMethodNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);

        final LocalServer server =
                this.factory.createLocalServer(TEST_SCOPE, this.config);
        server.activate();

        final String methodName = "test";
        server.addMethod(methodName, new Callback() {

            @Override
            public Event internalInvoke(final Event request)
                    throws UserCodeException {
                // nothing to do for testing
                return null;
            }
        });

        server.deactivate();

        this.factory.removeObserver(observer);

        // string names required due to visibility of the class
        checkMethodSequence(observer.calls, "rsb.patterns.LocalMethod",
                methodName);

    }

    // false positive due to asserts in called method
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void remoteMethodNotifies() throws Throwable {

        final FactoryObserver observer = new FactoryObserver();
        this.factory.addObserver(observer);

        final RemoteServer server
            = this.factory.createRemoteServer(TEST_SCOPE, this.config);
        server.activate();

        final String methodName = "testXXXX";
        // "generate" a method by calling one and ignoring the result
        try {
            // CHECKSTYLE.OFF: MagicNumber - we just want to wait as short as
            // possible
            server.call(methodName, 0.001);
            // CHECKSTYLE.ON: MagicNumber
        } catch (final TimeoutException e) {
            // expected since we call something that doesn't exist
        }

        server.deactivate();

        this.factory.removeObserver(observer);

        // string names required due to visibility of the class
        checkMethodSequence(observer.calls, "rsb.patterns.RemoteMethod",
                methodName);

    }

}
