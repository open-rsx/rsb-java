/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.RsbTestCase;

/**
 * @author dklotz
 */
public class TimeLimitedQueueTest extends RsbTestCase {

    private static final int WINDOW_MILLIS = 100;

    private static final int CAPACITY = 10;

    private TimeLimitedQueue queue;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.queue =
                new TimeLimitedQueue(CAPACITY, WINDOW_MILLIS,
                        TimeUnit.MILLISECONDS);
    }

    @Test
    public void addEvent() {
        // CHECKSTYLE.OFF: MagicNumber - hard to write otherwise
        final Event firstEvent = new Event();
        final long firstTime = firstEvent.getMetaData().getCreateTime();

        assertTrue(this.queue.add(firstEvent));

        final Event aBitLater = new Event();
        aBitLater.getMetaData().setCreateTime(
                firstTime
                        + TimeUnit.MICROSECONDS.convert(10,
                                TimeUnit.MILLISECONDS));
        assertTrue(this.queue.add(aBitLater));
        assertEquals(2, this.queue.size());

        final Event lateButGood = new Event();
        lateButGood.getMetaData().setCreateTime(
                firstTime
                        + TimeUnit.MICROSECONDS.convert(99,
                                TimeUnit.MILLISECONDS));
        assertTrue(this.queue.add(lateButGood));
        assertEquals(3, this.queue.size());

        final Event overTheLimit = new Event();
        overTheLimit.getMetaData().setCreateTime(
                firstTime
                        + TimeUnit.MICROSECONDS.convert(105,
                                TimeUnit.MILLISECONDS));
        assertTrue(this.queue.add(overTheLimit));
        assertEquals(3, this.queue.size());

        assertEquals(aBitLater.getMetaData().getCreateTime(), this.queue.peek()
                .getMetaData().getCreateTime());
        assertSame(aBitLater, this.queue.peek());

        final Event evenLater = new Event();
        evenLater.getMetaData().setCreateTime(
                firstTime
                        + TimeUnit.MICROSECONDS.convert(150,
                                TimeUnit.MILLISECONDS));
        assertTrue(this.queue.add(evenLater));
        assertEquals(3, this.queue.size());

        assertSame(lateButGood, this.queue.poll());
        assertSame(overTheLimit, this.queue.poll());
        assertSame(evenLater, this.queue.poll());

        assertEquals(0, this.queue.size());
        // CHECKSTYLE.ON: MagicNumber
    }

}
