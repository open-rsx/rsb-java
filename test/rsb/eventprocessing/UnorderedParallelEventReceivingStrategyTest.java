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
package rsb.eventprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Event;
import rsb.AbstractEventHandler;
import rsb.eventprocessing.UnorderedParallelEventReceivingStrategy;

/**
 * @author swrede
 */
public class UnorderedParallelEventReceivingStrategyTest {

	private final class TestHandler extends AbstractEventHandler {
		boolean notified = false;
		public Event event;

		public boolean isNotified() {
			return notified;
		}

		@Override
		public void handleEvent(Event e) {
			event = e;
			notified = true;
		}
	}

	@Test
	public final void testEventDispatcher() {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		assertNotNull(ed);
		// TODO test configuration
	}

	@Test
	public final void testEventDispatcherIntIntInt() {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy(
				1, 3, 100);
		assertNotNull(ed);
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#addHandler(rsb.event.Handler)}
	 * .
	 */
	@Test
	public final void testAddHandler() {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler h = new TestHandler();
		ed.addHandler(h, true);
		assertTrue(ed.getHandlers().contains(h));
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#removeHandler(rsb.event.Handler)}
	 * .
	 */
	@Test
	public final void testRemoveSubscription() throws InterruptedException {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler h = new TestHandler();
		ed.addHandler(h, true);
		ed.removeHandler(h, true);
		assertTrue(ed.getHandlers().size() == 0);
	}

	private AbstractEventHandler getHandler() {
		return new TestHandler();
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#fire(rsb.Event)}
	 * .
	 *
	 * @throws InterruptedException
	 */
	@Test
	public final void testFire() throws InterruptedException {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler l = (TestHandler) getHandler();
		ed.addHandler(l, true);
		long beforeFire = System.currentTimeMillis() * 1000;
		Event event = new Event();
		ed.handle(event);
		ed.shutdownAndWait();
		long afterShutdown = System.currentTimeMillis() * 1000;
		assertTrue(l.isNotified());
		assertSame(event, l.event);
		assertTrue(event.getMetaData().getDeliverTime() >= beforeFire);
		assertTrue(event.getMetaData().getDeliverTime() <= afterShutdown);
	}
}
