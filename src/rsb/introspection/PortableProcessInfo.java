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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cross-platform plain Java implementation of process info interface.
 *
 * @author swrede
 */
public class PortableProcessInfo extends CommonProcessInfo {

    private static final Logger LOG = Logger
            .getLogger(PortableProcessInfo.class.getName());

    public PortableProcessInfo() {
        super();
        this.initialize();
    }

    private void initialize() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

        // Get name returns something like 6460@AURORA. Where the value
        // before the @ symbol is the PID.
        final String jvmName = runtime.getName();
        try {
            this.pid = Integer.valueOf(jvmName.split("@")[0]);
        } catch (final NumberFormatException e) {
            LOG.log(Level.INFO,
                    "Exception when parsing pid (RuntimeMXBean.getName()=="
                            + jvmName + ")", e);
        }

        this.startTime = runtime.getStartTime();

        this.name = "java-" + System.getProperty("java.runtime.version");
        // Returns the input arguments passed to the Java virtual machine which
        // does
        // not include the arguments to the main method. This method returns an
        // empty
        // list if there is no input argument to the Java virtual machine.
        this.arguments = runtime.getInputArguments();
    }

}
