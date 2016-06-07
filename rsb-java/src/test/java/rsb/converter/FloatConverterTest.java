/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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
 * @author jwienke
 */
public class FloatConverterTest extends LoggingEnabled {

    private static final int SERIALIZED_NUM_BYTES = 4;

    @Test
    public void serialize() throws Throwable {
        final FloatConverter converter = new FloatConverter();
        final Float input = 1.2345f;
        final WireContents<ByteBuffer> buf =
                converter.serialize(Float.class, input);
        assertNotNull(buf);
        // check expected format hand-calculated
        final byte[] converted = buf.getSerialization().array();
        final byte[] expected = new byte[] { 25, 4, -98, 63 };
        assertEquals(SERIALIZED_NUM_BYTES, converted.length);
        for (int i = 0; i < SERIALIZED_NUM_BYTES; i++) {
            assertEquals(expected[i], converted[i]);
        }
    }

    @Test
    public void roundtripSmallValue() throws Throwable {
        final FloatConverter converter = new FloatConverter();
        final Float input = 0.542f;
        final WireContents<ByteBuffer> buf =
                converter.serialize(Float.class, input);
        assertNotNull(buf);
        final Object output =
                converter.deserialize(buf.getWireSchema(),
                        buf.getSerialization()).getData();
        final Float outputLong = (Float) output;
        assertEquals(input, outputLong);
    }

    @Test
    public void roundtripLargeValue() throws Throwable {
        final FloatConverter converter = new FloatConverter();
        final Float input = 130236144.123f;
        final WireContents<ByteBuffer> buf =
                converter.serialize(Float.class, input);
        assertNotNull(buf);
        final Object output =
                converter.deserialize(buf.getWireSchema(),
                        buf.getSerialization()).getData();
        final Float outputLong = (Float) output;
        assertEquals(input, outputLong);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotALongError() throws Throwable {
        final FloatConverter converter = new FloatConverter();
        converter.serialize(Float.class, new LinkedList<Integer>());
    }

}
