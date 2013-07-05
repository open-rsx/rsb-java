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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.AbstractEventHandler;
import rsb.Event;

/**
 * @author swrede
 */
public class UnorderedParallelEventReceivingStrategyTest {

    private final class TestHandler extends AbstractEventHandler {

        boolean notified = false;
        public Event event;

        public boolean isNotified() {
            return this.notified;
        }

        @Override
        public void handleEvent(final Event e) {
            this.event = e;
            this.notified = true;
        }
    }

    @Test
    public final void eventDispatcher() {
        final UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
        assertNotNull(ed);
        // TODO test configuration
    }

    @Test
    public final void eventDispatcherIntIntInt() {
        final UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy(
                1, 3, 100);
        assertNotNull(ed);
    }

    @Test
    public final void addHandler() {
        final UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
        final TestHandler h = new TestHandler();
        ed.addHandler(h, true);
        assertTrue(ed.getHandlers().contains(h));
    }

    @Test
    public final void removeSubscription() throws InterruptedException {
        final UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
        final TestHandler h = new TestHandler();
        ed.addHandler(h, true);
        ed.removeHandler(h, true);
        assertTrue(ed.getHandlers().size() == 0);
    }

    @Test
    public final void fire() throws InterruptedException {
        final UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
        final TestHandler l = new TestHandler();
        ed.addHandler(l, true);
        final long beforeFire = System.currentTimeMillis() * 1000;
        final Event event = new Event();
        ed.handle(event);
        ed.shutdownAndWait();
        final long afterShutdown = System.currentTimeMillis() * 1000;
        assertTrue(l.isNotified());
        assertSame(event, l.event);
        assertTrue(event.getMetaData().getDeliverTime() >= beforeFire);
        assertTrue(event.getMetaData().getDeliverTime() <= afterShutdown);
    }
}
