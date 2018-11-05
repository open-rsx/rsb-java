/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 Jan Moringen
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
package rsb.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.Scope;
import rsb.EventId;
import rsb.Event;
import rsb.RsbTestCase;
import rsb.ParticipantId;

/**
 * Unit tests for the {@link rsb.filter.CauseFilter} class.
 *
 * @author jmoringe
 */
public class CauseFilterTest extends RsbTestCase {

    static final Scope SCOPE = new Scope("/foo");
    static final String PAYLOAD = "foo";

    @Test
    public void smoke() {
        final Event event1 = new Event(SCOPE, String.class, PAYLOAD);
        final EventId id1 = new EventId(new ParticipantId(), 1);
        event1.addCause(id1);

        final Event event2 = new Event(SCOPE, String.class, PAYLOAD);
        final EventId id2 = new EventId(new ParticipantId(), 2);
        event2.addCause(id2);

        final CauseFilter filter1 = new CauseFilter(id1);
        assertTrue(filter1.match(event1));
        assertFalse(filter1.match(event2));

        final CauseFilter filter2 = new CauseFilter(id1, true);
        assertFalse(filter2.match(event1));
        assertTrue(filter2.match(event2));
    }

};
