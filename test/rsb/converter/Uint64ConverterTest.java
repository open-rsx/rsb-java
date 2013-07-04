/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
import java.util.LinkedList;

import org.junit.Test;

/**
 * @author jmoringe
 */
public class Uint64ConverterTest {

    @Test
    public void serialize() throws Throwable {
        final Uint64Converter c = new Uint64Converter();
        final Long l = 2431709L;
        final WireContents<ByteBuffer> buf = c.serialize(Long.class, l);
        assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
        final Uint64Converter c = new Uint64Converter();
        {
            final Long l1 = 24398L;
            final WireContents<ByteBuffer> buf = c.serialize(Long.class, l1);
            assertNotNull(buf);
            final Object o = c.deserialize(buf.getWireSchema(),
                    buf.getSerialization()).getData();
            final Long l2 = (Long) o;
            assertEquals(l1, l2);
        }

        {
            final Long l1 = 130236144L;
            final WireContents<ByteBuffer> buf = c.serialize(Long.class, l1);
            assertNotNull(buf);
            final Object o = c.deserialize(buf.getWireSchema(),
                    buf.getSerialization()).getData();
            final Long l2 = (Long) o;
            assertEquals(l1, l2);
        }
    }

    @Test(expected = ConversionException.class)
    public void serializationNotALongError() throws Throwable {
        final Uint64Converter c = new Uint64Converter();
        c.serialize(Long.class, new LinkedList<Integer>());
    }

}
