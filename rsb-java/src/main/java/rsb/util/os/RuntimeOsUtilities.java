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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to retrieve OS and process information from a
 * {@link java.lang.management.RuntimeMXBean} instance. The dependency to this
 * class is explicitly hidden via reflection to support Android devices, where
 * the management package does not exist.
 *
 * @author jwienke
 */
public final class RuntimeOsUtilities {

    private static final Logger LOG = Logger.getLogger(RuntimeOsUtilities.class
            .getName());

    private static final String PID_HOST_SEPARATOR = "@";

    private final Object runtime;

    private Method getInputArguments;
    private Method getStartTime;
    private Method getName;

    /**
     * Indicates that a {@link java.lang.management.RuntimeMXBean} instance is
     * not available on this system.
     *
     * @author jwienke
     */
    public static class RuntimeNotAvailableException extends RuntimeException {

        private static final long serialVersionUID = 8231745636666959997L;

        /**
         * Constructor.
         */
        public RuntimeNotAvailableException() {
            super();
        }

        /**
         * Constructor.
         *
         * @param message
         *            explanation
         * @param cause
         *            cause
         */
        public RuntimeNotAvailableException(final String message,
                final Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructor.
         *
         * @param message
         *            explanation
         */
        public RuntimeNotAvailableException(final String message) {
            super(message);
        }

        /**
         * Constructor.
         *
         * @param cause
         *            cause
         */
        public RuntimeNotAvailableException(final Throwable cause) {
            super(cause);
        }
    }

    /**
     * Creates a new instance.
     */
    public RuntimeOsUtilities() {
        try {
            final Class<?> factoryClass =
                    Class.forName("java.lang.management.ManagementFactory");
            final Class<?> runtimeInterface =
                    Class.forName("java.lang.management.RuntimeMXBean");
            final Method getRuntimeMxBean =
                    factoryClass.getMethod("getRuntimeMXBean");
            this.runtime = getRuntimeMxBean.invoke(null);
            this.getInputArguments =
                    runtimeInterface.getMethod("getInputArguments");
            this.getStartTime = runtimeInterface.getMethod("getStartTime");
            this.getName = runtimeInterface.getMethod("getName");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeNotAvailableException(e);
        } catch (final SecurityException e) {
            throw new RuntimeNotAvailableException(e);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeNotAvailableException(e);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeNotAvailableException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeNotAvailableException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeNotAvailableException(e);
        }
    }

    private static void logReflectionError(final String methodName,
            final Exception exception) {
        LOG.log(Level.WARNING,
                "Error during reflection-based method call in method "
                        + methodName, exception);
    }

    /**
     * Returns the input arguments passed to the Java virtual machine which does
     * not include the arguments to the main method. This method returns an
     * empty list if there is no input argument to the Java virtual machine.
     *
     * @return list of arguments, <code>null</code> if no determinable on
     *         current system
     */
    @SuppressWarnings("unchecked")
    public List<String> determineArguments() {
        Exception error = null;
        try {
            return (List<String>) this.getInputArguments.invoke(this.runtime);
        } catch (final IllegalArgumentException e) {
            error = e;
        } catch (final IllegalAccessException e) {
            error = e;
        } catch (final InvocationTargetException e) {
            error = e;
        }
        logReflectionError("determineArguments", error);
        return null;
    }

    /**
     * Returns the start time of the process in milliseconds.
     *
     * @return start time in milliseconds unix epoch, <code>null</code> if no
     *         determinable on current system
     */
    public Long determineStartTime() {
        Exception error = null;
        try {
            return (Long) this.getStartTime.invoke(this.runtime);
        } catch (final IllegalArgumentException e) {
            error = e;
        } catch (final IllegalAccessException e) {
            error = e;
        } catch (final InvocationTargetException e) {
            error = e;
        }
        logReflectionError("determineStartTime", error);
        return null;
    }

    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    private String[] getPidAndHost() {
        Exception error = null;
        try {
            // getName returns something like 6460@AURORA. Where the value
            // before the @ symbol is the PID.
            final String jvmName = (String) this.getName.invoke(this.runtime);

            if (!jvmName.contains(PID_HOST_SEPARATOR)) {
                throw new IllegalArgumentException(String.format(
                        "Unable to find separator character %s "
                                + "in jvm name '%s'", PID_HOST_SEPARATOR,
                        jvmName));
            }

            final String[] jvmNameParts = jvmName.split(PID_HOST_SEPARATOR);
            if (jvmNameParts.length != 2 || jvmNameParts[0].isEmpty()
                    || jvmNameParts[1].isEmpty()) {
                throw new IllegalArgumentException(String.format(
                        "jvm name '%s' does not have the "
                                + "expected format pid@host", jvmName));
            }

            return jvmNameParts;

        } catch (final IllegalArgumentException e) {
            error = e;
        } catch (final IllegalAccessException e) {
            error = e;
        } catch (final InvocationTargetException e) {
            error = e;
        }
        logReflectionError("getPidAndHost", error);
        return null;
    }

    /**
     * Returns the PID of the java process.
     *
     * @return pid of the process, <code>null</code> if no determinable on
     *         current system
     */
    public Integer determinePid() {
        final String[] pidAndHost = getPidAndHost();
        if (pidAndHost != null) {
            try {
                return Integer.valueOf(pidAndHost[0]);
            } catch (final NumberFormatException e) {
                LOG.log(Level.WARNING, "Unable to parse PID", e);
            }
        }
        return null;
    }

    /**
     * Optionally, removes the domains parts from a fully qualified domain name.
     * If no fully qualified name is given, nothing happens.
     *
     * @param hostName
     *            host name to work with, not <code>null</code>, not empty or
     *            dot character
     * @return host name without domain parts.
     * @throws IllegalArgumentException
     *             in case the given host name is empty
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
     * {@link java.lang.management.RuntimeMXBean} lives on.
     *
     * @return host name, <code>null</code> if no determinable on current system
     * @throws IllegalArgumentException
     *             in case the host name could not be parsed from the runtime
     *             environment
     */
    public String determineHostName() {
        final String[] pidAndHost = getPidAndHost();
        if (pidAndHost != null) {
            try {
                return removeDomainFromHostName(pidAndHost[1]);
            } catch (final IllegalArgumentException e) {
                LOG.log(Level.WARNING, "Unable to determine host name", e);
            }
        }
        return null;
    }
}
