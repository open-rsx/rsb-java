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
package rsb.util;

import java.util.logging.Logger;

import org.junit.Test;


/**
 * @author swrede
 *
 */
public class OSDetectorTest {

    private static Logger LOG = Logger.getLogger(OSDetectorTest.class.getName());

    /**
     * Test method for {@link rsb.util.OSDetector#getOSFamily()}.
     */
    @Test
    public void testGetOSFamily() {
        LOG.info("Detected " + OSDetector.getOSFamily().name() + " OS family.");
    }
}
