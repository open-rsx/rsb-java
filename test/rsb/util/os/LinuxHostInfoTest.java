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
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Test for {@link LinuxHostInfo}.
 *
 * @author jwienke
 */
public class LinuxHostInfoTest {

    private static final String NON_EXISTING_PATH =
            "i/do/not/exists.txttxtxtxt";
    private static final String DBUS_MACHINE_ID_PATH = "dbus-machine-id.txt";
    private static final String EXPECTED_DBUS_ID =
            "031e3caa88117eac88bce57e5485b9c5";

    @Test
    public void machineIdSmoke() throws Throwable {

        final File dbusIdFile =
                new File(getClass().getResource(DBUS_MACHINE_ID_PATH).getPath());
        final List<File> machineIdCascade = new LinkedList<File>();
        machineIdCascade.add(dbusIdFile);

        final LinuxHostInfo info = new LinuxHostInfo(machineIdCascade);
        assertEquals(EXPECTED_DBUS_ID, info.getHostId());

    }

    @Test
    public void machineIdNotFound() throws Throwable {

        final List<File> machineIdCascade = new LinkedList<File>();
        machineIdCascade.add(new File(NON_EXISTING_PATH));

        final LinuxHostInfo info = new LinuxHostInfo(machineIdCascade);
        assertNull(info.getHostId());

    }

    @Test
    public void machineIdFallback() throws Throwable {

        final List<File> machineIdCascade = new LinkedList<File>();
        machineIdCascade.add(new File(NON_EXISTING_PATH));
        final File dbusIdFile =
                new File(getClass().getResource(DBUS_MACHINE_ID_PATH).getPath());
        machineIdCascade.add(dbusIdFile);

        final LinuxHostInfo info = new LinuxHostInfo(machineIdCascade);
        assertEquals(EXPECTED_DBUS_ID, info.getHostId());

    }

    @Test
    public void machineIdPriorities() throws Throwable {

        final List<File> machineIdCascade = new LinkedList<File>();
        final File dbusIdFile =
                new File(getClass().getResource(DBUS_MACHINE_ID_PATH).getPath());
        machineIdCascade.add(dbusIdFile);
        final File confusionFile =
                new File(getClass().getResource("machine-id-confusion.txt")
                        .getPath());
        machineIdCascade.add(confusionFile);

        final LinuxHostInfo info = new LinuxHostInfo(machineIdCascade);
        assertEquals(EXPECTED_DBUS_ID, info.getHostId());

    }

    @Test
    public void machineIdEmptySkipped() throws Throwable {

        final List<File> machineIdCascade = new LinkedList<File>();
        machineIdCascade.add(new File(getClass().getResource(
                "empty-id-file.txt").getPath()));

        final LinuxHostInfo info = new LinuxHostInfo(machineIdCascade);
        assertNull(info.getHostId());

    }
}
