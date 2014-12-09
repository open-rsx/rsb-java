/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
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
package rsb.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to perform various tasks related to details about the
 * underlying operating system of a process.
 *
 * @author swrede
 * @author jwienke
 */
public final class OsUtilities {

    private static final Logger LOG = Logger.getLogger(OsUtilities.class
            .getName());

    private OsUtilities() {
        // prevent utility class instantiation
    }

    /**
     * Enumeration describing different operating system families.
     *
     * @author swrede
     */
    public static enum OsFamily {

        /**
         * Operating system family for linux-based systems.
         */
        LINUX,

        /**
         * Operating system family for windows-based systems (including 64 bit
         * windows).
         */
        WINDOWS,

        /**
         * Operating system family for Mac (OS X) systems.
         */
        DARWIN,

        /**
         * Any other unknown operating system.
         */
        UNKNOWN

    }

    /**
     * Returns the name of the operating system a process is currently being
     * operated on.
     *
     * @return a non-empty string
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /**
     * Tries to determine the operating system family from a concrete operating
     * system name returned by {@link #getOsName()}.
     *
     * @param identifier
     *            the concrete operating system name, not <code>null</code>
     * @return identified operating system family.
     */
    public static OsFamily deriveOsFamily(final String identifier) {
        assert identifier != null;
        if (identifier.startsWith("Windows")) {
            return OsFamily.WINDOWS;
        } else if (identifier.startsWith("Linux")) {
            return OsFamily.LINUX;
        } else if (identifier.startsWith("Mac")) {
            return OsFamily.DARWIN;
        } else {
            return OsFamily.UNKNOWN;
        }
    }

    /**
     * Enum to describe the bit architecture of a computer.
     *
     * @author swrede
     */
    public static enum MachineType {

        /**
         * Any 32 bit computer.
         */
        X86,

        /**
         * 64 bit computers.
         */
        X86_64,

        /**
         * Any other bit architecture.
         */
        UNKNOWN

    }

    /**
     * Returns the architecture of the operating system a process is being
     * operated on.
     *
     * @return non-empty name string, not <code>null</code>
     */
    public static String getOsArchitecture() {
        return System.getProperty("os.arch");
    }

    /**
     * Tries to guess a class of os architectures from a name given from
     * {@link #getOsArchitecture()}.
     *
     * @param identifier
     *            name of os architecture, not <code>null</code>
     * @return guessed architecture class, not <code>null</code>
     */
    public static MachineType deriveMachineType(final String identifier) {
        assert identifier != null;
        if (identifier.contains("x86") || identifier.contains("i386")) {
            return MachineType.X86;
        } else if (identifier.startsWith("amd64")) {
            return MachineType.X86_64;
        } else {
            return MachineType.UNKNOWN;
        }
    }

    public static String getHostIdInet() throws IOException {
        final InetAddress ipAddress = InetAddress.getLocalHost();
        // creates problem when ip address is not resolved
        final NetworkInterface network =
                NetworkInterface.getByInetAddress(ipAddress);

        // might throw SocketException, which is a subclass of IOException and
        // hence ok
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
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            LOG.log(Level.WARNING,
                    "Exception while getting hostName via InetAddress.", e);
            return null;
        }
    }

}
