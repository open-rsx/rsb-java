/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011,2012 CoR-Lab, Bielefeld University
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
package rsb.examples;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import rsb.AbstractDataHandler;
import rsb.Factory;
import rsb.Listener;

/**
 * A basic example that demonstrated how to receive event payloads.
 *
 * @author swrede
 */
public class DataListenerExample extends AbstractDataHandler<String> {

    private static final Logger LOG = Logger
            .getLogger(DataListenerExample.class.getName());

    static AtomicInteger counter = new AtomicInteger(0);
    static Object l = new Object();

    /**
     * The actual callback that is notified upon arrival of events. In contrast
     * to an EventListener, here the event payload is passed to the callback.
     */
    @Override
    public void handleEvent(final String data) {
        counter.getAndIncrement();
        if (counter.get() % 100 == 0) {
            LOG.info("Event #" + counter.get() + " received with payload: "
                    + data);
        }
        if (counter.get() == 1000) {
            synchronized (l) {
                l.notifyAll();
            }
        }
    }

    public static void main(final String[] args) throws Throwable {

        // get a factory instance to create new RSB domain objects
        final Factory factory = Factory.getInstance();

        // create a Listener instance on the specified scope that will receive
        // events and dispatches them asynchronously to all registered handlers
        final Listener sub = factory.createListener("/example/informer");

        // activate the listener to be ready for work
        sub.activate();

        // add a DataHandler, here the DataListenerExample that is notified
        // directly
        // with the data extracted from the received event
        sub.addHandler(new DataListenerExample(), true);

        // wait that enough events are received
        while (!allEventsDelivered()) {
            synchronized (l) {
                l.wait(1000);
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
