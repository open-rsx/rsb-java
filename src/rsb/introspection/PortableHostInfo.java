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

    /**
     * Creates a new instance and initializes provided variables.
     */
    public PortableHostInfo() {
        super();
        initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // after the @ symbol is the hostname.
        final String jvmName = runtime.getName();
        try {
            this.setHostName(jvmName.split("@")[1]);
        } catch (final ArrayIndexOutOfBoundsException e) {
            LOG.log(Level.INFO,
                    "Exception when parsing hostname (RuntimeMXBean.getName()=="
                            + jvmName + ")", e);
            this.setHostName("unknown");
        }

        // try to calculate hostId via the network
        try {
            this.setId(getHostIdInet());
            if (this.getHostName().equalsIgnoreCase("unknown")) {
                this.setHostName(this.getHostId());
            }
            return;
        } catch (final IOException e) {
            LOG.log(Level.WARNING,
                    "Unexpected I/O exception when getting MAC address", e);
        }
        // try to get local hostname via network
        this.setId(getLocalHostName());
        if (this.getHostId() == null) {
            // last resort: get hostname via ManagementBean
            this.setId(this.getHostName());
        }
    }

}
