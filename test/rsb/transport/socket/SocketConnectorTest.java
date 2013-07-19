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
package rsb.transport.socket;

import rsb.transport.ConnectorCheck;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;

/**
 * Test for socket connectors.
 * 
 * @author jwienke
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class SocketConnectorTest extends ConnectorCheck {

    @Override
    protected InPushConnector createInConnector() throws Throwable {
        return new SocketInPushConnector(
                rsb.transport.socket.Utilities.getSocketOptions(),
                ServerMode.AUTO, this.getConverterStrategy("utf-8-string"));
    }

    @Override
    protected OutConnector createOutConnector() throws Throwable {
        return new SocketOutConnector(Utilities.getSocketOptions(),
                ServerMode.AUTO, this.getConverterStrategy(String.class
                        .getName()));
    }

}
