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

import rsb.LoggingEnabled;

/**
 * @author jmoringe
 */
public class LongConverterTest extends LoggingEnabled {

    @Test
    public void serialize() throws Throwable {
        final LongConverter converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        final Long input = 2431709L;
        final WireContents<ByteBuffer> buf =
                converter.serialize(Long.class, input);
        assertNotNull(buf);
    }

    @Test
    public void roundtripSmallValue() throws Throwable {
        final LongConverter converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        final Long input = 24398L;
        final WireContents<ByteBuffer> buf =
                converter.serialize(Long.class, input);
        assertNotNull(buf);
        final Object output =
                converter.deserialize(buf.getWireSchema(),
                        buf.getSerialization()).getData();
        final Long outputLong = (Long) output;
        assertEquals(input, outputLong);
    }

    @Test
    public void roundtripLargeValue1() throws Throwable {
        final LongConverter converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        final Long input = 130236144L;
        final WireContents<ByteBuffer> buf =
            converter.serialize(Long.class, input);
        assertNotNull(buf);
        final Object output =
            converter.deserialize(buf.getWireSchema(),
                                  buf.getSerialization()).getData();
        final Long outputLong = (Long) output;
        assertEquals(input, outputLong);
    }

    @Test
    public void roundtripLargeValue2() throws Throwable {
        final LongConverter converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        final Long input = 6172840431323434035L;
        final WireContents<ByteBuffer> buf =
            converter.serialize(Long.class, input);
        assertNotNull(buf);
        final Object output =
            converter.deserialize(buf.getWireSchema(),
                                  buf.getSerialization()).getData();
        final Long outputLong = (Long) output;
        assertEquals(input, outputLong);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotALongError() throws Throwable {
        final LongConverter converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        converter.serialize(Long.class, new LinkedList<Integer>());
    }

}
