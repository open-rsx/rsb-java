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
package rsb.transport;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rsb.Event;
import rsb.EventId;
import rsb.RsbTestCase;
import rsb.ParticipantId;
import rsb.Scope;

// CHECKSTYLE.OFF: MagicNumber - test values
// CHECKSTYLE.OFF: JavadocMethod - test class

/**
 * Base class for a generic roundtrip check for Connectors.
 *
 * @author jwienke
 */
public abstract class ConnectorRoundtripCheck extends RsbTestCase {

    private final int dataSize;

    protected ConnectorRoundtripCheck(final int dataSize) {
        this.dataSize = dataSize;
    }

    @Test(timeout = 4000)
    public void roundtrip() throws Throwable {

        final Scope scope = new Scope("/a/test/scope");

        final InPushConnector inPort = createInConnector();
        final OutConnector outPort = createOutConnector();

        final List<Event> receivedEvents = new ArrayList<Event>();

        inPort.addHandler(new EventHandler() {

            @Override
            public void handle(final Event event) {
                synchronized (receivedEvents) {
                    receivedEvents.add(event);
                    receivedEvents.notifyAll();
                }
            }

        });

        inPort.setScope(scope);
        inPort.activate();
        outPort.setScope(scope);
        outPort.activate();

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.dataSize; ++i) {
            builder.append('c');
        }

        final Event event = new Event(scope, String.class, builder.toString());
        event.setId(new ParticipantId(), 42);
        event.getMetaData().setUserInfo("foo", "a long string");
        event.getMetaData().setUserInfo("barbar", "a long string again");
        event.getMetaData().setUserTime("asdasd", 324234);
        event.getMetaData().setUserTime("xxx", 42);
        event.addCause(new EventId(new ParticipantId(), 23434));
        event.addCause(new EventId(new ParticipantId(), 42));

        outPort.push(event);

        synchronized (receivedEvents) {
            while (receivedEvents.size() != 1) {
                receivedEvents.wait();
            }

            final Event receivedEvent = receivedEvents.get(0);

            // normalize times as they are not important for this test
            event.getMetaData().setReceiveTime(
                    receivedEvent.getMetaData().getReceiveTime());

            assertEquals(event, receivedEvent);
        }

        inPort.deactivate();
        outPort.deactivate();

    }

    protected abstract OutConnector createOutConnector() throws Throwable;

    protected abstract InPushConnector createInConnector() throws Throwable;

}

//CHECKSTYLE.ON: JavadocMethod
//CHECKSTYLE.ON: MagicNumber
