/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.introspection;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cross-platform plain Java implementation of host info interface.
 *
 * @author swrede
 */
public class PortableHostInfo extends CommonHostInfo {

    private static final Logger LOG = Logger.getLogger(PortableHostInfo.class
            .getName());

    public PortableHostInfo() {
        super();
        this.initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // after the @ symbol is the hostname.
        final String jvmName = runtime.getName();
        try {
            this.hostname = jvmName.split("@")[1];
        } catch (final ArrayIndexOutOfBoundsException e) {
            LOG.log(Level.INFO,
                    "Exception when parsing hostname (RuntimeMXBean.getName()=="
                            + jvmName + ")", e);
            this.hostname = "unknown";
        }

        // try to calculate hostId via the network
        try {
            this.id = getHostIdInet();
            if (this.hostname.equalsIgnoreCase("unknown")) {
                this.hostname = this.id;
            }
            return;
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when getting MAC address: "
                    + e.getMessage());
            e.printStackTrace();
        }
        // try to get local hostname via network
        this.id = getLocalHostName();
        if (this.id == null) {
            // last resort: get hostname via ManagementBean
            this.id = this.getHostname();
        }
    }

}
