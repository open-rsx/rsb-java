/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
 * Copyright (C) 2011 David Klotz <david -at- sofaecke -dot- org>
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

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rsb.Event;

/**
 * @author dklotz
 *
 */
public class TimeLimitedQueueTest {

	private static final int CAPACITY = 10;

	private TimeLimitedQueue queue;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		queue = new TimeLimitedQueue(CAPACITY, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * Test method for {@link de.unibi.corlab.kuha.utils.TimeLimitedQueue#add(rsb.Event)}.
	 */
	@Test
	public void testAddEvent() {
		Event firstEvent = new Event();
		long firstTime = firstEvent.getMetaData().getCreateTime();

		assertTrue(queue.add(firstEvent));

		Event aBitLater = new Event();
		aBitLater.getMetaData().setCreateTime(firstTime + TimeUnit.MICROSECONDS.convert(10, TimeUnit.MILLISECONDS));
		assertTrue(queue.add(aBitLater));
		assertEquals(2, queue.size());

		Event lateButGood = new Event();
		lateButGood.getMetaData().setCreateTime(firstTime + TimeUnit.MICROSECONDS.convert(99, TimeUnit.MILLISECONDS));
		assertTrue(queue.add(lateButGood));
		assertEquals(3, queue.size());

		Event overTheLimit = new Event();
		overTheLimit.getMetaData().setCreateTime(firstTime + TimeUnit.MICROSECONDS.convert(105, TimeUnit.MILLISECONDS));
		assertTrue(queue.add(overTheLimit));
		assertEquals(3, queue.size());

		assertEquals(aBitLater.getMetaData().getCreateTime(), queue.peek().getMetaData().getCreateTime());
		assertSame(aBitLater, queue.peek());

		Event evenLater = new Event();
		evenLater.getMetaData().setCreateTime(firstTime + TimeUnit.MICROSECONDS.convert(150, TimeUnit.MILLISECONDS));
		assertTrue(queue.add(evenLater));
		assertEquals(3, queue.size());

		assertSame(lateButGood, queue.poll());
		assertSame(overTheLimit, queue.poll());
		assertSame(evenLater, queue.poll());

		assertEquals(0, queue.size());
	}

}
