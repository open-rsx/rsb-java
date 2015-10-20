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
package rsb.eventprocessing;

import java.net.URI;
import java.util.Set;
import java.util.logging.Logger;

import rsb.Event;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.OutConnector;

/**
 * Class implementing the basic out route configuration based on a push
 * strategy.
 *
 * @author jwienke
 */
public class DefaultOutRouteConfigurator implements OutRouteConfigurator {

    private static final Logger LOG = Logger
            .getLogger(DefaultOutRouteConfigurator.class.getName());

    private final RouteConfiguratorUtility<OutConnector> utility;

    /**
     * Constructor.
     *
     * @param scope
     *            the scope the out route operates on
     */
    public DefaultOutRouteConfigurator(final Scope scope) {
        this.utility = new RouteConfiguratorUtility<OutConnector>(scope);
    }

    @Override
    public void activate() throws RSBException {
        this.utility.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.utility.deactivate();
    }

    @Override
    public boolean isActive() {
        return this.utility.isActive();
    }

    @Override
    public void addConnector(final OutConnector connector) {
        this.utility.addConnector(connector);
    }

    @Override
    public boolean removeConnector(final OutConnector connector) {
        return this.utility.removeConnector(connector);
    }

    @Override
    public void publishSync(final Event event) throws RSBException {
        for (final OutConnector connector : this.utility.getConnectors()) {
            LOG.finer("Pushing event to connector " + connector);
            connector.push(event);
        }
    }

    @Override
    public Set<URI> getTransportUris() {
        return this.utility.getTransportUris();
    }

    // TODO method for setting quality of service specs on the connectors

}
