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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import rsb.RsbTestCase;

/**
 * Test for {@link LinuxProcessInfo}.
 *
 * @author jwienke
 */
public class LinuxProcessInfoTest extends RsbTestCase {

    private static final long TEST_START_TIME = 1418232564050000L;

    @Test
    public void smoke() throws Throwable {

        final LinuxProcessInfo info =
                new LinuxProcessInfo(new File(getClass().getResource(
                        "cmdline-correct.txt").getPath()), new File(getClass()
                        .getResource("proc-stat.txt").getPath()), new File(
                        getClass().getResource("proc-self-stat.txt").getPath()));
        assertTrue(info.getProgramName().startsWith("my cp with spaces"));
        assertEquals(
                Arrays.asList(new String[] {
                    "/proc/self/cmdline",
                    "test/rsb/util/cmdline-correct.txt",
                }),
                info.getArguments());
        assertNotNull(info.getStartTime());
        assertEquals(Long.valueOf(TEST_START_TIME), info.getStartTime());

    }

    @Test
    public void nonExistingFiles() throws Throwable {
        final LinuxProcessInfo info =
                new LinuxProcessInfo(new File("fhadjkfahsd"), new File(
                        "afa34fsdafsdf"), new File("6wbjdrgq45ag"));
        assertNotNull(info.getProgramName());
        assertEquals(null, info.getArguments());
        assertEquals(null, info.getStartTime());
    }

}
