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
package rsb.transport.spread;

import org.junit.Test;

import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.Listener;
import rsb.LoggingEnabled;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;
import rsb.config.TransportConfig;
import rsb.patterns.DataCallback;
import rsb.patterns.LocalServer;
import rsb.patterns.RemoteServer;
import rsb.util.Properties;

/**
 * Test for situations where no connection to a Spread daemon is available.
 *
 * Pre-Condition: Spread is not running
 *
 * @author swrede
 * @author jwienke
 */
public class SpreadNotRunningTest extends LoggingEnabled {

    private static final Scope SCOPE = new Scope("/test/");

    private ParticipantConfig createParticipantConfig() throws Throwable {
        final ParticipantConfig config = new ParticipantConfig();
        config.setIntrospectionEnabled(false);
        final TransportConfig spreadConfig =
                config.getOrCreateTransport("spread");
        spreadConfig.setEnabled(true);
        final Properties spreadOptions = new Properties();
        // 4 should be a free port according to some port lists
        spreadOptions.setProperty("transport.spread.port", "4");
        spreadConfig.setOptions(spreadOptions);
        return config;
    }

    @Test(expected = InitializeException.class)
    public void informer() throws Throwable {
        final Factory factory = Factory.getInstance();
        final Informer<Object> informer =
                factory.createInformer(SCOPE, createParticipantConfig());
        informer.activate();
    }

    @Test(expected = InitializeException.class)
    public void listener() throws Throwable {
        final Factory factory = Factory.getInstance();
        final Listener listener =
                factory.createListener(SCOPE, createParticipantConfig());
        listener.activate();
    }

    @Test(expected = InitializeException.class)
    public void server() throws Throwable {
        final Factory factory = Factory.getInstance();
        final LocalServer server =
                factory.createLocalServer(SCOPE, createParticipantConfig());
        server.addMethod("foobar", new DataCallback<String, String>() {

            @Override
            public String invoke(final String request) throws Throwable {
                return "";
            }

        });
        server.activate();
    }

    @Test(expected = RSBException.class)
    public void remoteServer() throws Throwable {
        final Factory factory = Factory.getInstance();
        final RemoteServer server =
                factory.createRemoteServer(SCOPE, createParticipantConfig());
        server.activate();
        try {
            // this method fails and not activate since the real participants
            // are only created on the first use of a method
            server.call("foo");
        } finally {
            try {
                server.deactivate();
            } catch (final Exception e) {
                // ignore since we are most likely in an invalid state
            }
        }
    }

}
