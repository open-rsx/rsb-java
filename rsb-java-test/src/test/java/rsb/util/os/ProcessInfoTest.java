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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rsb.RsbTestCase;

/**
 * @author swrede
 */
public class ProcessInfoTest extends RsbTestCase {

    @Test
    public void testProcessInfo() {
        final ProcessInfo info = ProcessInfoSelector.getProcessInfo();
        assertNotNull(info.getUserName());
        assertFalse(info.getUserName().isEmpty());
        assertNotNull(info.getPid());
        assertNotNull(info.getProgramName());
        assertFalse(info.getProgramName().isEmpty());
        assertNotNull(info.getArguments());
        assertFalse(info.getArguments().isEmpty());
        assertNotNull(info.getStartTime());
        assertNotEquals(0, info.getStartTime().longValue());
    }

}
