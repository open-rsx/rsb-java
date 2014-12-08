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

import rsb.util.OsDetector;
import rsb.util.OsFamily;

/**
 * Encapsulates common host info functionality shared between portable and
 * non-portable subclasses. This class contains variables and getters and
 * setters for all required attributes of {@link HostInfo}.
 *
 * @author swrede
 */
public abstract class CommonHostInfo implements HostInfo {

    private static final Logger LOG = Logger.getLogger(CommonHostInfo.class
            .getName());

    private String hostId;
    private String hostName;
    private OsFamily softwareType;
    private MachineType machineType;

    /**
     * Creates a new instance and pre-computes values for
     * {@link HostInfo#getSoftwareType()} and {@link HostInfo#getMachineType()}.
     */
    public CommonHostInfo() {
        this.setSoftwareType(OsDetector.getOSFamily());
        this.setMachineType(readMachineType());
    }

    @Override
    public String getHostId() {
        return this.hostId;
    }

    /**
     * Sets the unique host id variable.
     *
     * @param hostId
     *            unique id
     */
    protected void setId(final String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Sets the host name variable.
     *
     * @param hostName
     *            the host name
     */
    protected void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    @Override
    public OsFamily getSoftwareType() {
        return this.softwareType;
    }

    /**
     * Sets the operating system family variable.
     *
     * @param softwareType
     *            the operating system family
     */
    protected void setSoftwareType(final OsFamily softwareType) {
        this.softwareType = softwareType;
    }

    @Override
    public MachineType getMachineType() {
        return this.machineType;
    }

    /**
     * Sets the bit architecture variable.
     *
     * @param machineType
     *            the bit archtiecture
     */
    protected void setMachineType(final MachineType machineType) {
        this.machineType = machineType;
    }

    protected MachineType readMachineType() {
        // TODO check better way to get CPU architecture
        // or at least make sure that these keys are correct
        final String identifier = System.getProperty("os.arch");
        if (identifier.contains("x86") || identifier.contains("i386")) {
            return MachineType.X86;
        } else if (identifier.startsWith("amd64")) {
            return MachineType.X86_64;
        } else {
            return MachineType.UNKNOWN;
        }
    }

    protected String getHostIdInet() throws IOException {
        final InetAddress ipAddress = InetAddress.getLocalHost();
        // creates problem when ip address is not resolved
        final NetworkInterface network =
                NetworkInterface.getByInetAddress(ipAddress);

        final byte[] mac = network.getHardwareAddress();

        if (mac == null) {
            throw new IOException(
                    "Could not read MAC adress via NetworkInterface class.");
        }

        final StringBuilder stringRep = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            stringRep.append(String.format("%02X%s", mac[i],
                    (i < mac.length - 1) ? "-" : ""));
        }

        return stringRep.toString();
    }

    /**
     * Returns a host name derived from name resolution for localhost.
     *
     * @return host name or <code>null</code> if not detectable
     */
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
