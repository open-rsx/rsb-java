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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        try {
            this.setPid(RuntimeOsUtilities.determinePid(runtime));
        } catch (final IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Unable to determine PID", e);
        }
        this.setStartTime(RuntimeOsUtilities.determineStartTime(runtime));
        this.setArguments(RuntimeOsUtilities.determineArguments(runtime));
        this.setProgramName(determineProgramName());
    }

    private String determineProgramName() {
        return "java-" + System.getProperty("java.runtime.version");
    }

}
