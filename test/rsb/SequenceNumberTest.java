/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rsb.transport.SequenceNumber;

public class SequenceNumberTest {

    @Test
    public void overflowUsage() {
        // check max value
        assertEquals(((long) 2 * Integer.MAX_VALUE + 1),
                Long.parseLong("4294967295"));
        assertEquals(SequenceNumber.MAX_VALUE, Long.parseLong("4294967295"));

        // test int overflow in use
        SequenceNumber i = new SequenceNumber(Integer.MAX_VALUE);
        assertEquals(i.get(), Integer.MAX_VALUE);
        i.incrementAndGet();
        assertEquals(i.get(), (long) Integer.MAX_VALUE + 1);

        // test uint32 overflow in use
        i = new SequenceNumber(SequenceNumber.MAX_VALUE);
        assertEquals(i.get(), Long.parseLong("4294967295"));
        i.incrementAndGet();
        assertEquals(0, i.get());
    }

    @Test
    public void overflowInit() {
        // integer overflow in constructor
        final SequenceNumber i = new SequenceNumber(Integer.MAX_VALUE + 1);
        assertEquals(Integer.MAX_VALUE + 1, Integer.MIN_VALUE);
        assertEquals(i.get(), (long) Integer.MAX_VALUE + 1);
        i.incrementAndGet();
    }

}
