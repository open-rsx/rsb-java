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
package rsb.transport;

import rsb.transport.socket.SocketFactory;
import rsb.transport.spread.SpreadFactory;

/**
 * A class statically registering all directly implemented transports.
 *
 * @author jwienke
 */
public final class DefaultTransports {

    private static Boolean registered = false;

    private DefaultTransports() {
        super();
        // prevent instantiation of a utility class
    }

    /**
     * Registers the known transports. Can be called multiple times.
     */
    public static void register() {

        synchronized (registered) {

            if (registered) {
                return;
            }

            TransportRegistry.getDefaultInstance().registerTransport("spread",
                    new SpreadFactory());
            TransportRegistry.getDefaultInstance().registerTransport("socket",
                    new SocketFactory());

            registered = true;

        }

    }

}
