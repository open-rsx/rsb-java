/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
package rsb.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.Test;

import rsb.EventId;
import rsb.ParticipantId;

/**
 * @author jwienke
 */
public class EventIdConverterTest {

    @Test
    public void roundtrip() throws Throwable {
        final EventIdConverter converter = new EventIdConverter();

        final EventId id = new EventId(new ParticipantId(), 251);
        final WireContents<ByteBuffer> buf = converter.serialize(id.getClass(),
                id);
        assertNotNull(buf);
        final Object o = converter.deserialize(buf.getWireSchema(),
                buf.getSerialization()).getData();
        assertEquals(id, o);
    }

}
