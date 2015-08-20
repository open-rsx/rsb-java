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

import rsb.filter.Filter;
import rsb.transport.InConnector;

/**
 * Implementing classes provide outgoing communication routes for
 * {@link rsb.Participant}s.
 *
 * @author jwienke
 * @param <ConnectorType>
 *            The type of {@link rsb.transport.Connector} instances used by the
 *            implementing classes
 */
public interface InRouteConfigurator<ConnectorType extends InConnector> extends
        RouteConfigurator<ConnectorType> {

    /**
     * Called in case a new filter was added and should be reflected by this
     * route.
     *
     * @param filter
     *            the added filter
     */
    void filterAdded(Filter filter);

    /**
     * Called in a case a filter should be removed from the route.
     *
     * @param filter
     *            the filter to remove
     * @return <code>true</code> if the filter was already active and was
     *         removed now, else <code>false</code>
     */
    boolean filterRemoved(Filter filter);

}
