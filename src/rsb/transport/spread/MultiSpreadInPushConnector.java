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
package rsb.transport.spread;

import java.util.HashSet;
import java.util.Set;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.filter.AbstractFilterObserver;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;
import rsb.transport.spread.SpreadMultiReceiver.Subscription;

/**
 * A {@link SpreadInPushConnector} which implement connection sharing by using a
 * {@link SpreadMultiReceiver} instance.
 *
 * @author jwienke
 */
public class MultiSpreadInPushConnector extends AbstractFilterObserver
        implements InPushConnector {

    private final SpreadMultiReceiver spread;
    private Scope scope = null;
    private boolean active = false;

    private final Set<EventHandler> handlers = new HashSet<EventHandler>();

    private Subscription subscription = null;

    private final EventHandler handler = new EventHandler() {

        @Override
        public void handle(final Event event) {
            synchronized (MultiSpreadInPushConnector.this.handlers) {
                // CHECKSTYLE.OFF: LineLength - no way to avoid with eclipse
                for (final EventHandler handler : MultiSpreadInPushConnector.this.handlers) {
                    handler.handle(event);
                }
                // CHECKSTYLE.ON: LineLength
            }
        }

    };

    /**
     * Constructor.
     *
     * @param spread
     *            the spread receiver instance to use, not <code>null</code>.
     */
    public MultiSpreadInPushConnector(final SpreadMultiReceiver spread) {
        assert spread != null;
        this.spread = spread;
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // nothing to do here for receiving messages in spread
    }

    @Override
    public void setScope(final Scope scope) {
        if (isActive()) {
            throw new IllegalStateException(
                    "Scope can only be set if the connector is inactive");
        }
        this.scope = scope;
    }

    @Override
    public void activate() throws RSBException {
        if (isActive()) {
            throw new IllegalStateException("Connector already active");
        }

        if (this.scope == null) {
            throw new RSBException(
                    "Scope has not been set before activating the connector");
        }

        this.subscription = new Subscription(this.scope, this.handler);
        final boolean subscribed = this.spread.subscribe(this.subscription);
        assert subscribed;

        this.active = true;

    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (!isActive()) {
            throw new IllegalStateException("Connector is not active");
        }

        assert this.subscription != null;
        final boolean unsubscribed = this.spread.unsubscribe(this.subscription);
        assert unsubscribed;

        this.active = false;

    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void addHandler(final EventHandler handler) {
        assert handler != null;
        synchronized (this.handlers) {
            this.handlers.add(handler);
        }
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        assert handler != null;
        synchronized (this.handlers) {
            return this.handlers.remove(handler);
        }
    }

}
