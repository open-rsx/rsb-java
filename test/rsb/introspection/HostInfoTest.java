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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import rsb.util.OsUtilities;

/**
 * @author swrede
 */
public class HostInfoTest {

    private static final Logger LOG = Logger.getLogger(HostInfoTest.class
            .getName());

    @Test
    public void testHostInfo() {
        final HostInfo info;
        switch (OsUtilities.getOsFamily()) {
        case LINUX:
            info = new LinuxHostInfo();
            break;
        default:
            info = new PortableHostInfo();
            break;
        }
        // TODO add meaningful tests
        assertFalse(info.getHostName().isEmpty());
        LOG.log(Level.INFO, "Hostname is: " + info.getHostName());
        assertFalse(info.getHostId().isEmpty());
        LOG.log(Level.INFO, "Machine ID is: " + info.getHostId());
        assertNotNull(info.getMachineType());
        LOG.log(Level.INFO, "Machine type is: " + info.getMachineType().name());
        assertNotNull(info.getSoftwareType());
        LOG.log(Level.INFO, "Software type is: "
                + info.getSoftwareType().name());
    }

}
