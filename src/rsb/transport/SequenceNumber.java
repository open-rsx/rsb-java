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
 */
public class SequenceNumber {

    /**
     * Represents the highest possible value for a sequence number: uint32 max:
     * 4.294.967.295.
     */
    public static final long MAX_VALUE = (long) 2 * Integer.MAX_VALUE + 1;

    private AtomicInteger count = new AtomicInteger();

    /**
     * Constructor starting with 0 as the sequence number.
     */
    public SequenceNumber() {
        super();
    }

    /**
     * Constructor starting with the specified number for the internal counter.
     *
     * @param value
     *            start number
     */
    public SequenceNumber(final long value) {
        if (Math.abs(value) > (SequenceNumber.MAX_VALUE)) {
            throw new IllegalArgumentException("Value larger than uint32");
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
        synchronized (this) {
            // respect uint32 size
            final long inc = this.get() + 1;
            if (inc > SequenceNumber.MAX_VALUE) {
                // reset to zero
                this.count = new AtomicInteger();
            } else {
                this.count.incrementAndGet();
            }
            return this.count.get() & 0xffffffffL;
        }
    }

    /**
     * Returns the internal counter value.
     *
     * @return value
     */
    public long get() {
        return this.count.get() & 0xffffffffL;
    }

}
