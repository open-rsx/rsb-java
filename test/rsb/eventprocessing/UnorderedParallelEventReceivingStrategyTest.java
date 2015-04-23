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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.AbstractEventHandler;
import rsb.Event;
import rsb.LoggingEnabled;

/**
 * @author swrede
 */
public class UnorderedParallelEventReceivingStrategyTest extends LoggingEnabled {

    private final class TestHandler extends AbstractEventHandler {

        private boolean notified = false;
        private Event event;

        public boolean isNotified() {
            return this.notified;
        }

        @Override
        public void handleEvent(final Event event) {
            this.event = event;
            this.notified = true;
        }

        public Event getEvent() {
            return this.event;
        }

    }

    @Test
    public final void eventDispatcher() {
        final UnorderedParallelEventReceivingStrategy strategy =
                new UnorderedParallelEventReceivingStrategy();
        assertNotNull(strategy);
        // TODO test configuration
    }

    @Test
    public final void addHandler() throws Throwable {
        final UnorderedParallelEventReceivingStrategy strategy =
                new UnorderedParallelEventReceivingStrategy();
        strategy.activate();
        final TestHandler handler = new TestHandler();
        strategy.addHandler(handler, true);
        assertTrue(strategy.getHandlers().contains(handler));
    }

    @Test
    public final void removeSubscription() throws Throwable {
        final UnorderedParallelEventReceivingStrategy strategy =
                new UnorderedParallelEventReceivingStrategy();
        strategy.activate();
        final TestHandler handler = new TestHandler();
        strategy.addHandler(handler, true);
        strategy.removeHandler(handler, true);
        assertEquals(0, strategy.getHandlers().size());
    }

    @Test
    public final void fire() throws Throwable {
        final UnorderedParallelEventReceivingStrategy strategy =
                new UnorderedParallelEventReceivingStrategy();
        strategy.activate();
        final TestHandler handler = new TestHandler();
        strategy.addHandler(handler, true);
        final long beforeFire = System.currentTimeMillis() * 1000;
        final Event event = new Event();
        strategy.handle(event);
        strategy.deactivate();
        final long afterShutdown = System.currentTimeMillis() * 1000;
        assertTrue(handler.isNotified());
        assertSame(event, handler.getEvent());
        assertTrue(event.getMetaData().getDeliverTime() >= beforeFire);
        assertTrue(event.getMetaData().getDeliverTime() <= afterShutdown);
    }
}
