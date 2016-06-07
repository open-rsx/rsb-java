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

import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.util.ExactTime;
import rsb.util.os.RuntimeOsUtilities.RuntimeNotAvailableException;

/**
 * Cross-platform plain Java implementation of process info interface.
 *
 * @author swrede
 * @author jwienke
 */
public class PortableProcessInfo extends CommonProcessInfo {

    private static final Logger LOG = Logger
            .getLogger(PortableProcessInfo.class.getName());

    /**
     * Creates a new instance and initializes all provided values in
     * {@link CommonProcessInfo}.
     */
    public PortableProcessInfo() {
        super();

        try {
            final RuntimeOsUtilities runtimeUtilities =
                    new RuntimeOsUtilities();
            this.setPid(runtimeUtilities.determinePid());
            this.setStartTime(runtimeUtilities.determineStartTime());
            this.setArguments(runtimeUtilities.determineArguments());
        } catch (final RuntimeNotAvailableException e) {
            LOG.log(Level.WARNING,
                    "Unable to determine PID, process start time "
                            + "and arguments.", e);
            // fallback to current time
            setStartTime(ExactTime.currentTimeMicros());
        }

        this.setProgramName(determineProgramName());
    }

    private String determineProgramName() {
        final String javaBinary =
                "java-" + System.getProperty("java.runtime.version");
        try {
            return javaBinary + " " + determineMainClassName();
        } catch (final NoSuchElementException e) {
            return javaBinary;
        }
    }

    /**
     * Tries to determine the fully-qualified main class name for the process
     * this class is used in.
     *
     * @return fully qualified name, not <code>null</code>
     * @throws NoSuchElementException
     *             name cannot be determined
     */
    public static String determineMainClassName() {
        for (final Entry<Thread, StackTraceElement[]> entry : Thread
                .getAllStackTraces().entrySet()) {
            if (entry.getKey().getId() == 1) {
                final StackTraceElement[] trace = entry.getValue();
                if (trace.length < 1) {
                    break;
                }
                return trace[trace.length - 1].getClassName();
            }
        }
        throw new NoSuchElementException(
                "Main class name cannot be determined "
                        + "because the main thread cannot be found or "
                        + "its stack trace is empty.");
    }

}
