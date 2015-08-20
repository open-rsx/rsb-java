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
package rsb.util.os;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
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
        LINUX("linux"),

        /**
         * Operating system family for windows-based systems (including 64 bit
         * windows).
         */
        WINDOWS("win32"),

        /**
         * Operating system family for Mac (OS X) systems.
         */
        DARWIN("darwin"),

        /**
         * Any other unknown operating system.
         */
        UNKNOWN(null);

        private final String name;

        /**
         * Constructor.
         *
         * @param name
         *            string name representing this class of operating system.
         */
        private OsFamily(final String name) {
            this.name = name;
        }

        /**
         * Returns a string representation of the operating system family.
         *
         * @return string name or <code>null</code> in case of unknown families.
         */
        public String getName() {
            return this.name;
        }

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
     * Tries to determine a normalized operating system family from a concrete
     * operating system name returned by {@link #getOsName()}.
     *
     * @param identifier
     *            the concrete operating system name, not <code>null</code>
     * @return identified operating system family.
     */
    public static String deriveOsFamilyName(final String identifier) {
        final String lowerCase = identifier.toLowerCase(Locale.ROOT);
        if (lowerCase.startsWith("win")) {
            return OsFamily.WINDOWS.getName();
        } else if (lowerCase.startsWith("mac")
                || lowerCase.equals(OsFamily.DARWIN.getName())) {
            return OsFamily.DARWIN.getName();
        } else {
            return lowerCase;
        }
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
        final String normalized = deriveOsFamilyName(identifier);
        if (normalized.equals(OsFamily.WINDOWS.getName())) {
            return OsFamily.WINDOWS;
        } else if (normalized.equals(OsFamily.LINUX.getName())) {
            return OsFamily.LINUX;
        } else if (normalized.equals(OsFamily.DARWIN.getName())) {
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
         * A 32-bit system in the x86 family (sometimes called "i386",
         * "i686" or less commonly "i586" depending on the specific
         * model).
         */
        X86("x86"),

        /**
         * A 64-bit system in the x86 family (sometimes called
         * "amd64").
         */
        X86_64("x86_64"),

        /**
         * Any other bit architecture.
         */
        UNKNOWN(null);

        private final String name;

        /**
         * Constructor.
         *
         * @param name
         *            string name representing this class of machines.
         */
        private MachineType(final String name) {
            this.name = name;
        }

        /**
         * Returns a string representation of the represented machine type.
         *
         * @return string name or <code>null</code> in case of unknown types.
         */
        public String getName() {
            return this.name;
        }

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
     * Tries to determine a normalized machine type from a name returned by
     * {@link #getOsArchitecture()}.
     *
     * @param identifier
     *            name of machine type, not <code>null</code>
     * @return guessed machine type, not <code>null</code>
     */
    public static String deriveMachineTypeName(final String identifier) {
        final String lowerCase = identifier.toLowerCase(Locale.ROOT);
        if (lowerCase.equals(MachineType.X86.getName())
                || lowerCase.contains("i386") || lowerCase.contains("i586")
                || lowerCase.contains("i686")) {
            return MachineType.X86.getName();
        } else if (lowerCase.equals(MachineType.X86_64.getName())
                || (lowerCase.startsWith("amd64"))) {
            return MachineType.X86_64.getName();
        } else {
            return lowerCase;
        }
    }

    /**
     * Tries to guess a machine type from a name returned by
     * {@link #getOsArchitecture()}.
     *
     * @param identifier
     *            name of machine type, not <code>null</code>
     * @return guessed machine type, not <code>null</code>
     */
    public static MachineType deriveMachineType(final String identifier) {
        assert identifier != null;
        final String normalized = deriveMachineTypeName(identifier);
        if (normalized.equals(MachineType.X86.getName())) {
            return MachineType.X86;
        } else if (normalized.equals(MachineType.X86_64.getName())) {
            return MachineType.X86_64;
        } else {
            return MachineType.UNKNOWN;
        }
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
