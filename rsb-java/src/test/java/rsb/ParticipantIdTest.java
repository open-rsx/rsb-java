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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.util.UUIDTools;

/**
 * @author swrede
 */
public class ParticipantIdTest extends LoggingEnabled {

    private static final String KNOWN_ID =
            "2a4b89df-d5a2-4671-af2e-7e7f7ff8961d";

    // this will merely test that nothing is thrown in constructor
    @SuppressWarnings({ "unused", "PMD.JUnitTestsShouldIncludeAssert" })
    @Test
    public void eventId() {
        new ParticipantId();
    }

    @Test
    public void eventIdString() {
        final ParticipantId participantId = new ParticipantId();
        final String stringRep = participantId.toString();
        final ParticipantId idFromString = new ParticipantId(stringRep);
        assertEquals(participantId, idFromString);
    }

    @Test
    public void toStringGeneration() {
        final ParticipantId participantId = new ParticipantId();
        final String stringRep = participantId.toString();
        assertTrue(stringRep.length() > 0);
    }

    @Test
    public void toByteArray() {
        final ParticipantId participantId = new ParticipantId(KNOWN_ID);

        final byte[] data = new byte[UUIDTools.UUID_BYTE_REP_LENGTH];

        // CHECKSTYLE.OFF: MagicNumber - hand calculated
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
        // CHECKSTYLE.ON: MagicNumber

        assertArrayEquals(data, participantId.toByteArray());

    }

    @Test
    public void fromByteArray() {

        final byte[] data = new byte[UUIDTools.UUID_BYTE_REP_LENGTH];

        // CHECKSTYLE.OFF: MagicNumber - hand calculated
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
        // CHECKSTYLE.ON: MagicNumber

        assertEquals(new ParticipantId(KNOWN_ID), new ParticipantId(data));

    }

}
