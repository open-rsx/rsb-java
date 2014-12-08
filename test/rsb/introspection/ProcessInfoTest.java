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
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import rsb.util.OSDetector;

public class ProcessInfoTest {

    private static final Logger LOG = Logger.getLogger(ProcessInfoTest.class
            .getName());

    @Test
    public void testProcessInfo() {
        final ProcessInfo info;
        switch (OSDetector.getOSFamily()) {
        case LINUX:
            info = new LinuxProcessInfo();
            LOG.info("Created LinuxProcessInfo");
            break;
        default:
            info = new PortableProcessInfo();
            LOG.info("Created PortableProcessInfo");
            break;
        }
        // TODO add meaningful tests
        assertFalse(info.getUserName().isEmpty());
        LOG.log(Level.INFO, "User is: " + info.getUserName());
        assertTrue(info.getPid() != 0);
        LOG.log(Level.INFO, "PID is: " + info.getPid());
        assertTrue(!info.getProgramName().isEmpty());
        LOG.log(Level.INFO, "ProgramName is: " + info.getProgramName());
        assertTrue(info.getArguments().size() != 0);
        LOG.log(Level.INFO, "Arguments are: " + info.getArguments());
        assertTrue(info.getStartTime() != 0);
        LOG.log(Level.INFO, "Starttime is: " + info.getStartTime());
    }

}
