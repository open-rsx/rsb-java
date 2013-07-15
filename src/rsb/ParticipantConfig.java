/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb;

import java.util.ArrayList;
import java.util.List;

import rsb.config.TransportConfig;

/**
 * A class describing the configuration of Participant instances.
 *
 * @author swrede
 */
public class ParticipantConfig {

    private final ArrayList<TransportConfig> transports = new ArrayList<TransportConfig>();

    // TODO add all configuration options which belong to transports to
    // dedicated TransportConfigs
    public ParticipantConfig() {
        this.transports.add(new TransportConfig("spread"));
    }

    // TODO add handleOption
    // iterate over all transport.<NAME>.*
    // find or create transportConfig for <NAME>
    // push option to transportConfig

    public final List<TransportConfig> getTransports() {
        return this.transports;
    }

}
