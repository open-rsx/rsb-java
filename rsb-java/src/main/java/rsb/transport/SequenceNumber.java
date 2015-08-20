/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.transport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Atomic uint32 counter implementation respecting size of ProtocolBuffer uint32
 * type.
 *
 * @author swrede
 * @author jwienke
 */
public class SequenceNumber {

    /**
     * Represents the highest possible value for a sequence number: uint32 max:
     * 4.294.967.295.
     */
    public static final long MAX_VALUE = 4294967295L;

    /**
     * Mask to transfer a negative integer to a positive long representation.
     */
    private static final long MASK_NEG_TO_POS = 0xffffffffL;

    /**
     * An atomic integer representing the sequence numbers. Internally we use
     * the negative integer range for value > Integer.MAX, which still fit into
     * a uint32.
     */
    private final AtomicInteger count;

    /**
     * Constructor starting with 0 as the sequence number.
     */
    public SequenceNumber() {
        this(0);
    }

    /**
     * Constructor starting with the specified number for the internal counter.
     *
     * @param value
     *            start number within uint32 range
     * @throws IllegalArgumentException
     *             value outside uint32 range
     */
    public SequenceNumber(final long value) {
        if (value > (SequenceNumber.MAX_VALUE)) {
            throw new IllegalArgumentException("Value larger than uint32");
        } else if (value < 0) {
            throw new IllegalArgumentException("Value < 0.");
        }
        this.count = new AtomicInteger((int) value);
    }

    /**
     * Increment the internal counter and returns the new result in an atomic
     * fashion.
     *
     * @return new counter value
     */
    public long incrementAndGet() {
        return this.count.incrementAndGet() & MASK_NEG_TO_POS;
    }

    /**
     * Returns the internal counter value.
     *
     * @return value
     */
    public long get() {
        return this.count.get() & MASK_NEG_TO_POS;
    }

}
