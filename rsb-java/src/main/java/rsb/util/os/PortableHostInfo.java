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

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.util.os.RuntimeOsUtilities.RuntimeNotAvailableException;

/**
 * Cross-platform plain Java implementation of host info interface.
 *
 * @author swrede
 */
public class PortableHostInfo extends CommonHostInfo {

    private static final Logger LOG = Logger.getLogger(PortableHostInfo.class
            .getName());

    /**
     * Creates a new instance and initializes provided variables.
     */
    public PortableHostInfo() {
        super();
        initialize();
    }

    private void initialize() {
        // host name
        try {
            final RuntimeOsUtilities runtimeUtilities =
                    new RuntimeOsUtilities();
            this.setHostName(runtimeUtilities.determineHostName());
        } catch (final RuntimeNotAvailableException e) {
            LOG.log(Level.WARNING,
                    "Unable to determine host name from runtime. "
                            + "Falling back to network resolution.");
        }
        // runtimeUtilities.determineHostName can also return null, so we need
        // to check this after the exception handler and not only exclusively
        // inside it
        if (getHostName() == null) {
            fallbackHostNameResolution();
        }

    }

    private void fallbackHostNameResolution() {
        this.setHostName(OsUtilities.getLocalHostName());
        if (this.getHostName() == null) {
            LOG.warning("Host name could not be determined at all.");
        }
    }

}
