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

import rsb.Activatable;
import rsb.transport.Connector;

/**
 * Abstract interface for classes setting up event receiving or sending routes
 * from {@link rsb.Participant} instances to the concrete transport
 * implementations.
 *
 * @author jwienke
 *
 * @param <ConnectorType>
 *            The type of {@link Connector} instances used by implementing
 *            classes
 */
public interface RouteConfigurator<ConnectorType extends Connector> extends
        Activatable {

    /**
     * Adds a connector which will subsequently be used for for sending events.
     *
     * This method must only be called before the object is being activated
     * using {@link #activate()}.
     *
     * @param connector
     *            connector to add
     */
    void addConnector(ConnectorType connector);

    /**
     * Removes a connector, which will not receive events for sending
     * afterwards.
     *
     * This method must only be called before the object is being activated
     * using {@link #activate()}.
     *
     * @param connector
     *            the connector to remove
     * @return <code>true</code> if the connector was previously installed and
     *         hence remove now, else <code>false</code>
     */
    boolean removeConnector(ConnectorType connector);

    /**
     * Returns URIs describing the transports configured for this configurator.
     *
     * Only valid if activated.
     *
     * @return set of transport URIs, not <code>null</code>
     * @throws IllegalStateException
     *             instance is in wrong state to get these URIs
     */
    Set<URI> getTransportUris();

}
