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
        final String max = "4294967295";
        assertEquals((long) 2 * Integer.MAX_VALUE + 1, Long.parseLong(max));
        assertEquals(SequenceNumber.MAX_VALUE, Long.parseLong(max));

        // test int overflow in use
        SequenceNumber number = new SequenceNumber(Integer.MAX_VALUE);
        assertEquals(number.get(), Integer.MAX_VALUE);
        number.incrementAndGet();
        assertEquals(number.get(), (long) Integer.MAX_VALUE + 1);

        // test uint32 overflow in use
        number = new SequenceNumber(SequenceNumber.MAX_VALUE);
        assertEquals(number.get(), Long.parseLong(max));
        number.incrementAndGet();
        assertEquals(0, number.get());
    }

    @Test
    public void overflowInit() {
        // integer overflow in constructor
        final SequenceNumber number = new SequenceNumber(Integer.MAX_VALUE + 1);
        assertEquals(Integer.MAX_VALUE + 1, Integer.MIN_VALUE);
        assertEquals(number.get(), (long) Integer.MAX_VALUE + 1);
        number.incrementAndGet();
    }

}
