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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Linux-specific implementation of CommonHostInfo class. Tries to compute and
 * cache a unique machine id (and hostname information) from the following
 * locations: 1. /etc/machine-id 2. /var/lib/dbus/machine-id 3. MAC adress of
 * network interface 4. local hostname
 *
 * @author swrede
 * @author ssharma
 */
public class LinuxHostInfo extends CommonHostInfo {

    private static final Logger LOG = Logger.getLogger(LinuxHostInfo.class
            .getName());

    private static final String PATH_ETC_MACHINE_ID = "/etc/machine-id";
    private static final String PATH_VAR_LIB_DBUS_MACHINE_ID =
            "/var/lib/dbus/machine-id";

    public LinuxHostInfo() {
        super();
        initialize(PATH_ETC_MACHINE_ID, PATH_VAR_LIB_DBUS_MACHINE_ID);
    }

    public LinuxHostInfo(final String machineIdPath1,
            final String machineIdPath2) {
        super();
        initialize(machineIdPath1, machineIdPath2);
    }

    private void initialize(final String machineIdPath1,
            final String machineIdPath2) {
        this.id = readHostId(machineIdPath1, machineIdPath2);
        this.hostname = getLocalHostName();
    }

    private String readHostId(final String machineIdPath1,
            final String machineIdPath2) {
        final File f1 = new File(machineIdPath1);
        final File f2 = new File(machineIdPath2);
        // first option: read machineId files
        try {
            if (f1.exists() && !f1.isDirectory()) {
                return readMachineId(f1);
            } else if (f2.exists() && !f2.isDirectory()) {
                return readMachineId(f2);
            }
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when accessing machineId file: "
                    + e.getMessage());
            e.printStackTrace();
        }
        // otherwise try to calculate hostId via the network
        try {
            return getHostIdInet();
        } catch (final IOException e) {
            LOG.warning("Unexpected I/O exception when getting MAC address: "
                    + e.getMessage());
            e.printStackTrace();
        }
        // last resort: return local hostname
        return getLocalHostName();
    }

    private String readMachineId(final File file) throws IOException {
        String machineId = "N/A";
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            machineId = reader.readLine();
        } catch (final IOException exception) {
            LOG.warning("Could not read MachineId from path: "
                    + file.toString());
            exception.printStackTrace();
        } finally {
            reader.close();
        }
        return machineId;
    }
}
