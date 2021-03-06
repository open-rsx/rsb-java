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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Test case for {@link Event}.
 *
 * @author jwienke
 */
public class EventTest extends RsbTestCase {

    // for this test I want to test the different orders of calling equals
    // manually, so the warning about using assertEquals is explicitly not
    // wanted here
    @SuppressWarnings({ "PMD.JUnitTestContainsTooManyAsserts",
            "PMD.UseAssertEqualsInsteadOfAssertTrue" })
    @Test
    public void comparison() {

        final Event event1 = new Event();
        final Event event2 = new Event();
        event2.getMetaData()
                .setCreateTime(event1.getMetaData().getCreateTime());
        assertEquals(event1, event2);

        // // TODO add Id test
        // meta1.setSenderId(new Id());
        // assertFalse(meta1.equals(meta2));
        // assertFalse(meta2.equals(meta1));
        //
        // meta2.setSenderId(meta1.getSenderId());
        // assertEquals(meta1, meta2); // identical
        //
        // meta1.setSenderId(new Id());
        // assertFalse(meta1.equals(meta2)); // distinct UUIDs, again

        event1.setScope(new Scope("/foo/bar"));
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setScope(new Scope("/other/scope"));
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setScope(event1.getScope());
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));

        event1.setType("blubb".getClass());
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setType(Boolean.class);
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setType(event1.getType());
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));

        event1.setData(new ArrayList<Integer>());
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setData("no match data");
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setData(event1.getData());
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));

        final int randomSequence = 23;
        event1.setId(new ParticipantId(), randomSequence);
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.setId(event1.getId().getParticipantId(), event1.getId()
                .getSequenceNumber());
        assertTrue(event1.equals(event2));
        assertTrue(event2.equals(event1));

        event1.setMethod("REQUEST");
        assertFalse(event1.equals(event2));
        event2.setMethod("Blub");
        assertFalse(event1.equals(event2));
        event2.setMethod("request");
        assertTrue(event1.equals(event2));

        // causes
        final EventId cause1 = new EventId(new ParticipantId(), 343);
        event1.addCause(cause1);
        assertFalse(event1.equals(event2));
        assertFalse(event2.equals(event1));
        event2.addCause(cause1);
        assertEquals(event1, event2);

    }

    @Test
    public void hashCodeGeneration() {

        final Event event1 = new Event();
        final Event event2 = new Event();
        event2.getMetaData()
                .setCreateTime(event1.getMetaData().getCreateTime());
        assertEquals(event1.hashCode(), event2.hashCode());

        // causes
        final int randomSequence = 234234;
        event1.addCause(new EventId(new ParticipantId(), randomSequence));
        assertNotSame(event1.hashCode(), event2.hashCode());

    }

    @Test
    public void testToString() {
        // ensure that an empty event can be converted to a string
        new Event().toString();
    }

}
