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

/**
 * @author swrede
 * @author jwienke
 */
public class SequenceNumberTest extends RsbTestCase {

    @Test
    public void defaultConstructor() {
        assertEquals(0, new SequenceNumber().get());
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void constructNegative() {
        new SequenceNumber(-1);
    }

    @Test
    public void increment() {
        assertEquals(1, new SequenceNumber().incrementAndGet());
    }

    @Test
    public void maxValue() {
        // CHECKSTYLE.OFF: MagicNumber - no real value in this specific case
        assertEquals(SequenceNumber.MAX_VALUE, (1L << 32L) - 1L);
        // CHECKSTYLE.ON: MagicNumber
    }

    @Test
    public void usesLongRange() {
        final SequenceNumber number = new SequenceNumber(Integer.MAX_VALUE);
        assertEquals(number.get(), Integer.MAX_VALUE);
        assertEquals(number.incrementAndGet(), Long.valueOf(Integer.MAX_VALUE) + 1);
        assertEquals(number.incrementAndGet(), Long.valueOf(Integer.MAX_VALUE) + 2);
    }

    @Test
    public void rangeOverflow() {
        final SequenceNumber number = new SequenceNumber(SequenceNumber.MAX_VALUE);
        assertEquals(number.get(), SequenceNumber.MAX_VALUE);
        assertEquals(0, number.incrementAndGet());
    }

}
