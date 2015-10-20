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
package rsb.transport.inprocess;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rsb.Event;
import rsb.transport.EventHandler;
import rsb.util.os.HostInfo;
import rsb.util.os.HostInfoSelector;
import rsb.util.os.ProcessInfo;
import rsb.util.os.ProcessInfoSelector;

/**
 * Implement an inprocess channel for passing events between different
 * connectors. Ingoing events via the {@link #push(Event)} method are
 * synchronously dispatched to all registered {@link EventHandler}s, which
 * usually should be {@link InPushConnector} instances.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.ShortClassName")
public class Bus {

    private final Set<EventHandler> handlers = Collections
            .synchronizedSet(new HashSet<EventHandler>());

    private final HostInfo hostInfo = HostInfoSelector.getHostInfo();
    private final ProcessInfo processInfo =
            ProcessInfoSelector.getProcessInfo();

    /**
     * Dispatch an event to all registered handlers.
     *
     * @param event
     *            the event to dispatch
     */
    public void push(final Event event) {
        event.getMetaData().setReceiveTime(0);
        synchronized (this.handlers) {
            for (final EventHandler handler : this.handlers) {
                handler.handle(event);
            }
        }
    }

    /**
     * Add a handler to receive new events.
     *
     * @param handler
     *            the handler
     */
    public void addHandler(final EventHandler handler) {
        this.handlers.add(handler);
    }

    /**
     * Removes a handler from the event receiving. Once removed, no new events
     * will be received.
     *
     * @param handler
     *            the handler
     */
    public void removeHandler(final EventHandler handler) {
        this.handlers.remove(handler);
    }

    /**
     * Return the URI describing the transport manifested by this bus.
     *
     * Only valid if activated.
     *
     * @return URI, not <code>null</code>
     * @throws IllegalStateException
     *             instance is in wrong state to prepare the URI
     */
    public URI getTransportUri() {
        try {
            return new URI("inprocess", null, this.hostInfo.getHostName(),
                    this.processInfo.getPid(), null, null, null);
        } catch (final URISyntaxException e) {
            assert false : "We do not add a path to the URI. "
                    + "Therefore it must always be valid.";
            throw new AssertionError(e);
        }
    }

}
