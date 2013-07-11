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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventIdTest {

    private final ParticipantId participantId = new ParticipantId();

    @Test
    public void equalsObject() {
        final EventId eventIdA = new EventId(this.participantId, 0);
        EventId eventIdB = new EventId(this.participantId, 0);
        assertEquals(eventIdA, eventIdB);
        assertTrue(eventIdA.getAsUUID().equals(eventIdB.getAsUUID()));
        eventIdB = new EventId(this.participantId, 1);
        assertFalse(eventIdA.getAsUUID().equals(eventIdB.getAsUUID()));
    }

    @Test
    public void getAsUUID() {
        final EventId eventId = new EventId(this.participantId, 1);
        assertNotNull(eventId.getAsUUID());
    }

}
