/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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
package rsb.examples;

// mark-start::body
import rsb.Factory;
import rsb.Listener;
import rsb.transport.spread.InPushConnectorFactoryRegistry;
import rsb.transport.spread.SharedInPushConnectorFactory;

/**
 * Explains how to enabled basic spread connection sharing for {@link Listener}
 * instances. The API to enable this is brittle and the feature in beta status.
 * So use this on your own risk.
 *
 * @author jwienke
 */
@SuppressWarnings("deprecation")
public class SharedConnectionListenerExample {

    public static void main(final String[] args) throws Throwable {
        // Get a factory instance to create new RSB objects.
        final Factory factory = Factory.getInstance();

        final String inPushFactoryKey = "shareIfPossible";
        // register a (spread-specifc) factory to create the appropriate in push
        // connectors. In this case the factory tries to share all connections
        // except the converters differ. You can implement other strategies to
        // better match your needs.
        InPushConnectorFactoryRegistry.getInstance().registerFactory(
                inPushFactoryKey, new SharedInPushConnectorFactory());

        // instruct the spread transport to use your newly registered factory
        // for creating in push connector instances
        factory.getDefaultParticipantConfig()
                .getOrCreateTransport("spread")
                .getOptions()
                .setProperty("transport.spread.java.infactory",
                        inPushFactoryKey);

        // Create several listeners as usual and have a look at the open
        // connections to the spread daemon, e.g. via netstat -tanp | grep 4803
        final Listener listenerA = factory.createListener("/example/informer");
        listenerA.activate();
        final Listener listenerB = factory.createListener("/example/informer2");
        listenerB.activate();
        final Listener listenerC = factory.createListener("/fooo");
        listenerC.activate();

        while (true) {
            Thread.sleep(1000);
        }

    }

}
// mark-end::body
