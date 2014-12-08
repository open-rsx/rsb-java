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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.util.OSDetector;
import rsb.util.OSFamily;

/**
 * Encapsulates common host info functionality shared between
 * portable and non-portable subclasses.
 *
 * @author swrede
 */
public abstract class CommonHostInfo implements HostInfo {

    private static final Logger LOG = Logger.getLogger(CommonHostInfo.class
            .getName());

    protected String id;
    protected String hostname;
    protected OSFamily softwareType;
    protected MachineType machineType;

    public CommonHostInfo() {
        this.softwareType = OSDetector.getOSFamily();
        this.machineType = readMachineType();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public OSFamily getSoftwareType() {
        return this.softwareType;
    }

    @Override
    public MachineType getMachineType() {
        return this.machineType;
    }

    protected MachineType readMachineType() {
        // TODO check better way to get CPU architecture
        // or at least make sure that these keys are correct
        final String identifier = System.getProperty("os.arch");
        if (identifier.contains("x86") || identifier.contains("i386")) {
            return MachineType.x86;
        } else if (identifier.startsWith("amd64")) {
            return MachineType.x86_64;
        } else {
            return MachineType.UNKNOWN;
        }
    }

    protected String getHostIdInet() throws IOException {
        final InetAddress ip = InetAddress.getLocalHost();
        // creates problem when ip address is not resolved
        final NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        final byte[] mac = network.getHardwareAddress();

        if (mac == null) {
            throw new IOException(
                    "Could not read MAC adress via NetworkInterface class.");
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i],
                    (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();
    }

    protected String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            LOG.log(Level.WARNING,
                    "Exception while getting hostName via InetAddress.", e);
            return null;
        }
    }

}
