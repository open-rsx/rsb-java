/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 Johannes Wienke
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

import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * Utility class to retrieve OS and process information from a
 * {@link RuntimeMXBean} instance.
 *
 * @author jwienke
 */
public final class RuntimeOsUtilities {

    private static final String PID_HOST_SEPARATOR = "@";

    private RuntimeOsUtilities() {
        // prevent utility class instantiation
    }

    /**
     * Returns the input arguments passed to the Java virtual machine which does
     * not include the arguments to the main method. This method returns an
     * empty list if there is no input argument to the Java virtual machine.
     *
     * @param runtime
     *            runtime to use as information provider
     * @return list of arguments, not <code>null</code>
     */
    public static List<String> determineArguments(final RuntimeMXBean runtime) {
        return runtime.getInputArguments();
    }

    /**
     * Returns the start time of the process in milliseconds.
     *
     * @param runtime
     *            runtime to use as information provider
     * @return start time in milliseconds unix epoch
     */
    public static long determineStartTime(final RuntimeMXBean runtime) {
        return runtime.getStartTime();
    }

    private static String[] getPidAndHost(final RuntimeMXBean runtime) {
        // getName returns something like 6460@AURORA. Where the value
        // before the @ symbol is the PID.
        final String jvmName = runtime.getName();

        if (!jvmName.contains(PID_HOST_SEPARATOR)) {
            throw new IllegalArgumentException(String.format(
                    "Unable to find separator character %s in jvm name '%s'",
                    PID_HOST_SEPARATOR, jvmName));
        }

        final String[] jvmNameParts = jvmName.split(PID_HOST_SEPARATOR);
        if (jvmNameParts.length != 2 || jvmNameParts[0].isEmpty()
                || jvmNameParts[1].isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "jvm name '%s' does not have the expected format pid@host",
                    jvmName));
        }

        return jvmNameParts;
    }

    /**
     * Returns the PID of the java process.
     *
     * @param runtime
     *            runtime to use as information provider
     * @return pid of the process
     * @throws IllegalArgumentException
     *             in case the PID could not be parsed from the runtime
     *             environment
     */
    public static int determinePid(final RuntimeMXBean runtime) {
        final String[] pidAndHost = getPidAndHost(runtime);
        // throwing the NumberFormatException here is ok since it is a subclass
        // of RuntimeException
        return Integer.valueOf(pidAndHost[0]);
    }

    /**
     * Optionally, removes the domains parts from a fully qualified domain name.
     * If no fully qualified name is given, nothing happens.
     *
     * @param hostName
     *            host name to work with, not <code>null</code>, not empty or
     *            dot character
     * @return host name without domain parts.
     */
    private static String removeDomainFromHostName(final String hostName) {
        final String[] parts = hostName.split("\\.");
        if (parts.length < 1) {
            throw new IllegalArgumentException("The given host name is empty.");
        }
        return parts[0];
    }

    /**
     * Returns the host name of the computer the JVM designated by the
     * {@link RuntimeMXBean} lives on.
     *
     * @param runtime
     *            runtime to use as information provider
     * @return host name, not <code>null</code>
     * @throws IllegalArgumentException
     *             in case the host name could not be parsed from the runtime
     *             environment
     */
    public static String determineHostName(final RuntimeMXBean runtime) {
        final String[] pidAndHost = getPidAndHost(runtime);
        return removeDomainFromHostName(pidAndHost[1]);
    }
}
