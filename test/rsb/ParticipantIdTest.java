/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author swrede
 * 
 */
public class ParticipantIdTest {

	/**
	 * Test method for {@link rsb.EventId#EventId()}.
	 */
	@Test
	public void testEventId() {		
		new ParticipantId();
	}

	/**
	 * Test method for {@link rsb.EventId#EventId(java.lang.String)}.
	 */
	@Test
	public void testEventIdString() {
		ParticipantId id = new ParticipantId();
		String s = id.toString();
		ParticipantId id2 = new ParticipantId(s);
		assertTrue(id.equals(id2));
	}

	/**
	 * Test method for {@link rsb.EventId#toString()}.
	 */
	@Test
	public void testToString() {
		ParticipantId id = new ParticipantId();
		String s = id.toString();
		assertTrue(s.length() > 0);
	}

	@Test
	public void testToByteArray() {
		ParticipantId id = new ParticipantId("2a4b89df-d5a2-4671-af2e-7e7f7ff8961d");

		byte[] data = new byte[16];

		data[0] = (byte) 42;
		data[1] = (byte) 75;
		data[2] = (byte) 137;
		data[3] = (byte) 223;
		data[4] = (byte) 213;
		data[5] = (byte) 162;
		data[6] = (byte) 70;
		data[7] = (byte) 113;
		data[8] = (byte) 175;
		data[9] = (byte) 46;
		data[10] = (byte) 126;
		data[11] = (byte) 127;
		data[12] = (byte) 127;
		data[13] = (byte) 248;
		data[14] = (byte) 150;
		data[15] = (byte) 29;

		assertArrayEquals(data, id.toByteArray());

	}

	@Test
	public void testFromByteArray() {

		byte[] data = new byte[16];

		data[0] = (byte) 42;
		data[1] = (byte) 75;
		data[2] = (byte) 137;
		data[3] = (byte) 223;
		data[4] = (byte) 213;
		data[5] = (byte) 162;
		data[6] = (byte) 70;
		data[7] = (byte) 113;
		data[8] = (byte) 175;
		data[9] = (byte) 46;
		data[10] = (byte) 126;
		data[11] = (byte) 127;
		data[12] = (byte) 127;
		data[13] = (byte) 248;
		data[14] = (byte) 150;
		data[15] = (byte) 29;

		assertEquals(new ParticipantId("2a4b89df-d5a2-4671-af2e-7e7f7ff8961d"), new ParticipantId(
				data));

	}

}
