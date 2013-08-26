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
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.filter.AbstractFilterObserver;
import rsb.transport.EventHandler;

/**
 * Implements a push-based connector for receiving events via the inprocess
 * transport.
 *
 * @author jwienke
 */
public class InPushConnector extends AbstractFilterObserver implements
        rsb.transport.InPushConnector, EventHandler {

    private final Bus bus;
    private Scope scope;
    private boolean active;
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
        this.bus = bus;
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // nothing to do here
    }

    @Override
    public void setScope(final Scope scope) {
        synchronized (this) {
            if (isActive()) {
                throw new IllegalStateException(
                        "Scope can only be set when not active.");
            }
            this.scope = scope;
        }

    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            if (isActive()) {
                throw new IllegalStateException("Already active");
            }
            if (this.scope == null) {
                throw new IllegalStateException(
                        "Scope must be set before activating a connector");
            }
            this.active = true;

            this.bus.addHandler(this);

        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            if (!isActive()) {
                throw new IllegalStateException("Not active");
            }
            this.active = false;

            this.bus.removeHandler(this);

        }
    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.active;
        }
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

            if (!event.getScope().equals(this.scope)
                    && !event.getScope().isSubScopeOf(this.scope)) {
                return;
            }

            synchronized (this.handlers) {
                for (final EventHandler handler : this.handlers) {
                    handler.handle(event);
                }
            }

        }

    }

}
