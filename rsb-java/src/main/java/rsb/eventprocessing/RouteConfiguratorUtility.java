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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.Scope;
import rsb.transport.Connector;

/**
 * A utility class that can be used via composition to implement some of the
 * methods required by the {@link RouteConfigurator} interface.
 *
 * @author jwienke
 *
 * @param <ConnectorType>
 *            type of connectors
 */
public class RouteConfiguratorUtility<ConnectorType extends Connector> {

    private static final Logger LOG = Logger
            .getLogger(RouteConfiguratorUtility.class.getName());

    private final List<ConnectorType> connectors =
            new ArrayList<ConnectorType>();
    private final Scope scope;
    private boolean active = false;

    /**
     * Constructor.
     *
     * @param scope
     *            the managed route operates on this scope
     */
    public RouteConfiguratorUtility(final Scope scope) {
        this.scope = scope;
    }

    /**
     * Activate the managed connectors.
     *
     * @throws RSBException
     *             error activating
     */
    public void activate() throws RSBException {

        synchronized (this) {

            assert !this.active;
            if (this.active) {
                LOG.warning("Will not activate because I was already active");
                return;
            }

            for (final ConnectorType connector : this.connectors) {
                LOG.finer("Activating connector " + connector);
                connector.setScope(this.scope);
                connector.activate();
            }

            this.active = true;

        }

    }

    /**
     * Deactivate the managed connectors.
     *
     * @throws RSBException
     *             error deactivating
     * @throws InterruptedException
     *             interrupted while waiting for connectors to terminate
     */
    public void deactivate() throws RSBException, InterruptedException {

        synchronized (this) {

            assert this.active;
            if (!this.active) {
                LOG.warning("Will not deactivate because I was not active");
                return;
            }

            for (final ConnectorType connector : this.connectors) {
                LOG.finer("Deactivating connector " + connector);
                connector.deactivate();
            }

            this.active = false;

        }

    }

    /**
     * Tells whether already activated.
     *
     * @return <code>true</code> if activated, else <code>false</code>
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Adds a connector to use in this route.
     *
     * @param connector
     *            the connector to add
     */
    public void addConnector(final ConnectorType connector) {
        this.connectors.add(connector);
    }

    /**
     * Removes a connector from the route if it existed.
     *
     * @param connector
     *            the connector to remove
     * @return <code>true</code> if the connector existed
     */
    public boolean removeConnector(final ConnectorType connector) {
        return this.connectors.remove(connector);
    }

    /**
     * Returns the connectors used on the managed route.
     *
     * @return list of connectors
     */
    public List<ConnectorType> getConnectors() {
        return this.connectors;
    }

    /**
     * Utility method to implement {@link RouteConfigurator#getTransportUris()}.
     *
     * Iterates all transports and returns their URIs.
     *
     * Might only be possible, depending on the transports, if activated before.
     *
     * @return set of transport URIs, not <code>null</code>
     * @throws IllegalStateException not activated
     */
    public Set<URI> getTransportUris() {
        final Set<URI> uris = new HashSet<URI>();
        for (final ConnectorType connector : this.connectors) {
            uris.add(connector.getTransportUri());
        }
        return uris;
    }

}
