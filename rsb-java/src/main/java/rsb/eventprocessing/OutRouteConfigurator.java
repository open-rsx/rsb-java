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

import rsb.Event;
import rsb.RSBException;
import rsb.transport.OutConnector;

/**
 * Implementing classes provide outgoing communication routes for
 * {@link rsb.Participant}s.
 *
 * @author jwienke
 */
public interface OutRouteConfigurator extends RouteConfigurator<OutConnector> {

    /**
     * Sends an event.
     *
     * This method must only be called after activating the object with
     * {@link #activate()}.
     *
     * @param event
     *            event to send
     * @throws RSBException
     *             sending error. e.g. impossible to convert data, transport
     *             error
     */
    void publishSync(Event event) throws RSBException;

}
