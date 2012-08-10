/*
 * Copyright 2010,2011 Bielefeld University
 * Copyright 2011 David Klotz <david -at- sofaecke -dot- org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
