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
package rsb.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;

import org.junit.Test;

import rsb.LoggingEnabled;

/**
 * @author swrede
 */
public class StringConverterTest extends LoggingEnabled {

    private static final String ASCII_WIRE_SCHEMA = "ascii-string";
    private static final String ASCII_ENCODING = "US-ASCII";

    @Test
    public void serialize() throws Throwable {
        final StringConverter converter = new StringConverter();
        final String input = "testcase";
        final WireContents<ByteBuffer> buf =
                converter.serialize(String.class, input);
        assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
        final StringConverter converter = new StringConverter();
        final String input = "testcase roundtrip";
        final WireContents<ByteBuffer> buf =
                converter.serialize(String.class, input);
        assertNotNull(buf);
        final Object output =
                converter.deserialize(buf.getWireSchema(),
                        buf.getSerialization()).getData();
        final String outputString = (String) output;
        assertEquals(input, outputString);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotAStringError() throws Throwable {
        final StringConverter converter = new StringConverter();
        converter.serialize(String.class, new LinkedList<Integer>());
    }

    @Test(expected = ConversionException.class)
    public void serializationEncodingError() throws Throwable {
        final String withNonAscii = "đħħ←ŋæ¶æŧđ";
        final StringConverter converter =
                new StringConverter(ASCII_ENCODING, ASCII_WIRE_SCHEMA);
        converter.serialize(String.class, withNonAscii);
    }

    @Test(expected = ConversionException.class)
    public void deserializationEncodingError() throws Throwable {
        final String withNonAscii = "đħħ←ŋæ¶æŧđui";
        final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        final ByteBuffer buffer = encoder.encode(CharBuffer.wrap(withNonAscii));
        final StringConverter converter =
                new StringConverter(ASCII_ENCODING, ASCII_WIRE_SCHEMA);
        converter.deserialize(ASCII_WIRE_SCHEMA, buffer);
    }

}
