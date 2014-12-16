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
package rsb.util.os;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.util.FileReadingUtilities;

/**
 * Linux-specific implementation of {@link CommonHostInfo}
 * class. Tries to compute and cache a unique machine id (and hostname
 * information) based on the following information:
 * <ol>
 * <li>contents of /etc/machine-id</li>
 * <li>contents of /var/lib/dbus/machine-id</li>
 * <li>local hostname</li>
 * </ol>
 *
 * @author swrede
 * @author ssharma
 * @author jwienke
 */
public class LinuxHostInfo extends PortableHostInfo {

    private static final Logger LOG = Logger.getLogger(LinuxHostInfo.class
            .getName());

    private static final String PATH_ETC_MACHINE_ID = "/etc/machine-id";
    private static final String PATH_DBUS_MACHINE_ID =
            "/var/lib/dbus/machine-id";

    /**
     * Creates a new instance and initializes all provided variables using
     * default paths for machine ids.
     */
    public LinuxHostInfo() {
        super();
        final List<File> defaultCascade = new LinkedList<File>();
        defaultCascade.add(new File(PATH_ETC_MACHINE_ID));
        defaultCascade.add(new File(PATH_DBUS_MACHINE_ID));
        initialize(defaultCascade);
    }

    /**
     * Creates a new instance with a user-defined cascade of files to check for
     * the first working machine id.
     *
     * @param machineIdCascade
     *            cascade of files to check for machine ids. The first file that
     *            contains a readable ID will be used
     */
    public LinuxHostInfo(final List<File> machineIdCascade) {
        super();
        initialize(machineIdCascade);
    }

    private void initialize(final List<File> machineIdCascade) {
        for (final File candidate : machineIdCascade) {
            this.setMachineId(readHostId(candidate));
            if (this.getHostId() != null) {
                break;
            }
        }
    }

    /**
     * Tries to read a file for a machine id.
     *
     * @param candidate
     *            file to try
     * @return id string, non-empty, <code>null</code> otherwise
     */
    private String readHostId(final File candidate) {

        try {

            final String machineId =
                    FileReadingUtilities.readFirstFileLine(candidate).trim();

            if (machineId.isEmpty()) {
                return null;
            } else {
                return machineId;
            }

        } catch (final IOException e) {
            LOG.log(Level.WARNING, "Could not read MachineId from path: {0}",
                    new Object[] { candidate });
            LOG.log(Level.WARNING, "Reason", e);
            return null;
        }

    }

}
