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

	ParticipantId participantId = new ParticipantId();

	@Test
	public void testEqualsObject() {
		EventId eventId_a = new EventId(participantId, 0);
		EventId eventId_b = new EventId(participantId, 0);
		assertEquals(eventId_a, eventId_b);
		assertTrue(eventId_a.getAsUUID().equals(eventId_b.getAsUUID()));
		eventId_b = new EventId(participantId, 1);
		assertFalse(eventId_a.getAsUUID().equals(eventId_b.getAsUUID()));
	}

	@Test
	public void testGetAsUUID() {
		EventId eventId = new EventId(participantId, 1);
		assertNotNull(eventId.getAsUUID());
	}

}
