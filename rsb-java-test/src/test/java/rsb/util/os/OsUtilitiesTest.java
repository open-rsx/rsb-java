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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rsb.RsbTestCase;
import rsb.util.os.OsUtilities.MachineType;
import rsb.util.os.OsUtilities.OsFamily;

/**
 * @author swrede
 */
public class OsUtilitiesTest extends RsbTestCase {

    @Test
    public void getOsName() {
        final String name = OsUtilities.getOsName();
        assertNotNull(name);
        assertFalse(name.isEmpty());
    }

    @Test
    public void deriveOsFamily() {
        // some random values from
        // http://www.java-gaming.org/index.php/topic,14110
        assertEquals(OsFamily.WINDOWS, OsUtilities.deriveOsFamily("Windows XP"));
        assertEquals(OsFamily.WINDOWS, OsUtilities.deriveOsFamily("Windows 98"));
        assertEquals(OsFamily.WINDOWS,
                OsUtilities.deriveOsFamily("Windows 2003"));
        assertEquals(OsFamily.WINDOWS,
                OsUtilities.deriveOsFamily("Windows 2000"));
        assertEquals(OsFamily.LINUX, OsUtilities.deriveOsFamily("Linux"));
        assertEquals(OsFamily.DARWIN, OsUtilities.deriveOsFamily("Mac OS X"));
        assertEquals(OsFamily.UNKNOWN, OsUtilities.deriveOsFamily("FreeBSD"));
        assertEquals(OsFamily.UNKNOWN, OsUtilities.deriveOsFamily(""));
        assertEquals(OsFamily.UNKNOWN, OsUtilities.deriveOsFamily("Not Linux"));
    }

    @Test
    public void getOsArchitecture() {
        final String name = OsUtilities.getOsArchitecture();
        assertNotNull(name);
        assertFalse(name.isEmpty());
    }

    @Test
    public void deriveMachineType() {
        // some random values from
        // http://www.java-gaming.org/index.php/topic,14110
        assertEquals(MachineType.X86, OsUtilities.deriveMachineType("x86"));
        assertEquals(MachineType.X86, OsUtilities.deriveMachineType("i386"));
        assertEquals(MachineType.X86_64, OsUtilities.deriveMachineType("amd64"));
        assertEquals(MachineType.UNKNOWN, OsUtilities.deriveMachineType("ppc"));
        assertEquals(MachineType.UNKNOWN,
                OsUtilities.deriveMachineType("blubb"));
        assertEquals(MachineType.UNKNOWN, OsUtilities.deriveMachineType(""));
    }

}
