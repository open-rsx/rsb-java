/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rsb.converter.DefaultConverters;
import rsb.patterns.Reader;

/**
 * User-level test for RSBJava.
 *
 * @author jwienke
 */
public class UserLevelTest extends RsbTestCase {

    private static final int EVENTS_TO_SEND = 100;

    @Test(timeout = 15000)
    public void roundtrip() throws Throwable {

        DefaultConverters.register();

        final Scope scope = new Scope("/example/informer");

        // set up a receiver for events
        final Set<String> receivedMessages = new HashSet<String>();
        final Listener listener =
                new Listener(new ListenerCreateArgs().setScope(scope)
                        .setConfig(Utilities.createParticipantConfig()));
        listener.activate();
        listener.addHandler(new AbstractEventHandler() {

            @Override
            public void handleEvent(final Event event) {
                synchronized (receivedMessages) {
                    receivedMessages.add((String) event.getData());
                    receivedMessages.notifyAll();
                }
            }
        }, true);

        final Informer<String> informer =
                new Informer<String>(new InformerCreateArgs().setScope(scope)
                        .setConfig(Utilities.createParticipantConfig()));
        informer.activate();

        try {

            // send events
            final Set<String> sentMessages = new HashSet<String>();
            for (int i = 0; i < EVENTS_TO_SEND; ++i) {
                final String message =
                        "<message val=\"Hello World!\" nr=\"" + i + "\"/>";
                informer.publish(message);
                sentMessages.add(message);
            }

            // wait for receiving all events that were sent
            synchronized (receivedMessages) {
                while (receivedMessages.size() < sentMessages.size()) {
                    receivedMessages.wait();
                }
            }

            assertEquals(sentMessages, receivedMessages);

        } finally {
            if (informer.isActive()) {
                try {
                    informer.deactivate();
                } catch (final Exception e) {
                    // ignore any error here
                }
            }
            if (listener.isActive()) {
                try {
                    listener.deactivate();
                } catch (final Exception e) {
                    // ignore any error here
                }
            }
        }

    }

    @Test(timeout = 15000)
    public void roundtripReader() throws Exception {

        DefaultConverters.register();

        final Scope scope = new Scope("/example/reader");

        // set up a receiver for events
        final Reader reader = new Reader(
                new ReaderCreateArgs()
                    .setConfig(Utilities.createParticipantConfig())
                    .setScope(scope),
                Factory.getInstance());
        reader.activate();

        final Informer<String> informer =
                new Informer<String>(new InformerCreateArgs().setScope(scope)
                        .setConfig(Utilities.createParticipantConfig()));
        informer.activate();

        try {

            // send events
            final Set<String> sentMessages = new HashSet<String>();
            for (int i = 0; i < EVENTS_TO_SEND; ++i) {
                final String message = "data " + i;
                informer.publish(message);
                sentMessages.add(message);
            }

            // read events
            final Set<String> receivedMessages = new HashSet<String>();
            for (int i = 0; i < EVENTS_TO_SEND; ++i) {
                receivedMessages.add((String) reader.read(
                            1, TimeUnit.SECONDS).getData());
            }

            assertEquals(sentMessages, receivedMessages);

        } finally {
            if (informer.isActive()) {
                try {
                    informer.deactivate();
                } catch (final Exception e) {
                    // ignore any error here
                }
            }
            if (reader.isActive()) {
                try {
                    reader.deactivate();
                } catch (final Exception e) {
                    // ignore any error here
                }
            }
        }

    }

}
