/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010, 2011, 2012 CoR-Lab, Bielefeld University
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

// mark-start::body
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import rsb.AbstractEventHandler;
import rsb.Factory;
import rsb.Event;
import rsb.Listener;

/**
 * A basic example that demonstrated how to receive events.
 *
 * @author swrede
 */
public class EventListenerExample extends AbstractEventHandler {

    private static final Logger LOG = Logger.getLogger(EventListenerExample.class.getName());

    static AtomicInteger counter = new AtomicInteger(0);
    static Object l = new Object();

    /**
     * The actual callback that is notified upon arrival of events. In contrast to
     * a DataListener, an Event object is passed to the callback which in addition
     * to the payload, also provides access to the Event meta data.
     */
    @Override
    public void handleEvent(Event event) {
        counter.getAndIncrement();
        if (counter.get() % 100 == 0) {
            LOG.info("Event #" + counter.get() + " received with payload: " + event.toString());
        }
        if (counter.get() == 1000) {
            synchronized (l) {
                l.notifyAll();
            }
        }
    }

    public static void main(String[] args) throws Throwable {

        // get a factory instance to create new RSB domain objects
        Factory factory = Factory.getInstance();

        // create a Listener instance on the specified scope that will receive
        // events and dispatches them asynchronously to all registered handlers
        Listener sub = factory.createListener("/example/informer");

        // activate the listener to be ready for work
        sub.activate();

        // add an EventHandler that will receive complete Event instances
        // whenever they are received
        sub.addHandler(new EventListenerExample(), true);

        // wait that enough events are received
        while (!allEventsDelivered()) {
            synchronized (l) {
                l.wait();
                LOG.fine("Wake-Up!!!");
            }
        }

        // as there is no explicit removal model in java, always manually
        // deactivate the listener if it is not needed anymore
        sub.deactivate();
    };

    private synchronized static boolean allEventsDelivered() {
        return !(counter.get() != 1000);
    }

}
// mark-end::body
