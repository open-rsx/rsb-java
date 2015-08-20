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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rsb.Event;
import rsb.RSBException;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.transport.EventHandler;

/**
 * Implements a push-based connector for receiving events via the inprocess
 * transport.
 *
 * @author jwienke
 */
public class InPushConnector extends ConnectorBase implements
        rsb.transport.InPushConnector, EventHandler {

    private final Set<EventHandler> handlers = Collections
            .synchronizedSet(new HashSet<EventHandler>());

    /**
     * Creates a new push based connector for receiving events from a given
     * {@link Bus} instance.
     *
     * @param bus
     *            the bus to receive from
     */
    public InPushConnector(final Bus bus) {
        super(bus);
    }

    @Override
    public void activate() throws RSBException {
        super.activate();
        getBus().addHandler(this);
    }

    @Override
    public void addHandler(final EventHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        return this.handlers.remove(handler);
    }

    @Override
    public void handle(final Event event) {

        synchronized (this) {

            if (!isActive()) {
                return;
            }

            if (!event.getScope().equals(getScope())
                    && !event.getScope().isSubScopeOf(getScope())) {
                return;
            }

            synchronized (this.handlers) {
                for (final EventHandler handler : this.handlers) {
                    handler.handle(event);
                }
            }

        }

    }

    @Override
    public void notify(final Filter filter, final FilterAction action) {
        // transport level filtering is currently not supported
    }

}
