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
package rsb.eventprocessing;

import java.net.URI;
import java.util.Set;

import rsb.AbstractActivatable;
import rsb.Handler;
import rsb.RSBException;
import rsb.Scope;
import rsb.filter.Filter;
import rsb.transport.InConnector;

/**
 * Default implementation of a {@link InRouteConfigurator}.
 *
 * @author jwienke
 * @author swrede
 */
public class DefaultInRouteConfigurator extends AbstractActivatable
                                            implements InRouteConfigurator {

    private EventReceivingStrategy receivingStrategy =
            new SingleThreadEventReceivingStrategy();
    private final RouteConfiguratorUtility<InConnector> utility;

    /**
     * Constructor.
     *
     * @param scope
     *            the scope events are received on
     */
    public DefaultInRouteConfigurator(final Scope scope) {
        this.utility = new RouteConfiguratorUtility<InConnector>(scope);
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this.utility) {
            for (final InConnector connector : this.utility.getConnectors()) {
                connector.addHandler(this.receivingStrategy);
            }
            this.utility.activate();
            this.receivingStrategy.activate();
        }
    }

    @Override
    public void deactivate() throws RSBException {
        synchronized (this.utility) {
            try {
                this.receivingStrategy.deactivate();
                this.utility.deactivate();
            } catch (final InterruptedException e) {
                // restore interrupted state for outer thread
                // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
                Thread.currentThread().interrupt();
            }
            for (final InConnector connector : this.utility.getConnectors()) {
                connector.removeHandler(this.receivingStrategy);
            }
        }
    }

    @Override
    public void filterAdded(final Filter filter) {
        this.receivingStrategy.addFilter(filter);
    }

    @Override
    public boolean filterRemoved(final Filter filter) {
        this.receivingStrategy.removeFilter(filter);
        return true;
    }

    @Override
    public void handlerAdded(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.receivingStrategy.addHandler(handler, wait);
    }

    @Override
    public boolean handlerRemoved(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.receivingStrategy.removeHandler(handler, wait);
        return true;
    }

    @Override
    public void addConnector(final InConnector connector) {
        this.utility.addConnector(connector);
    }

    @Override
    public boolean removeConnector(final InConnector connector) {
        return this.utility.removeConnector(connector);
    }

    @Override
    public boolean isActive() {
        return this.utility.isActive();
    }

    @Override
    public void
            setEventReceivingStrategy(final EventReceivingStrategy strategy) {
        synchronized (this.utility) {
            assert !this.utility.isActive();
            assert strategy != null;
            this.receivingStrategy = strategy;
        }
    }

    @Override
    public Set<URI> getTransportUris() {
        return this.utility.getTransportUris();
    }

}
