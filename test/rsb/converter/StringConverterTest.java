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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;

import org.junit.Test;

import rsb.converter.ConversionException;
import rsb.converter.StringConverter;
import rsb.converter.WireContents;

/**
 * @author swrede
 */
public class StringConverterTest {

	@Test
	public void serialize() throws Throwable {
		StringConverter c = new StringConverter();
		String s = "testcase";
		WireContents<ByteBuffer> buf = c.serialize(String.class, s);
		assertNotNull(buf);
	}

	@Test
	public void roundtrip() throws Throwable {
		StringConverter c = new StringConverter();
		String s1 = "testcase";
		WireContents<ByteBuffer> buf = c.serialize(String.class, s1);
		assertNotNull(buf);
		Object o = c.deserialize(buf.getWireSchema(), buf.getSerialization())
				.getData();
		String s2 = (String) o;
		assertEquals(s1, s2);
	}

	@Test(expected = ConversionException.class)
	public void serializationNotAStringError() throws Throwable {
		StringConverter c = new StringConverter();
		c.serialize(String.class, new LinkedList<Integer>());
	}

	@Test(expected = ConversionException.class)
	public void serializationEncodingError() throws Throwable {
		String withNonAscii = "đħħ←ŋæ¶æŧđ";
		StringConverter c = new StringConverter("US-ASCII", "ascii-string");
		c.serialize(String.class, withNonAscii);
	}

	@Test(expected = ConversionException.class)
	public void deserializationEncodingError() throws Throwable {
		String withNonAscii = "đħħ←ŋæ¶æŧđ";
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(withNonAscii));
		StringConverter c = new StringConverter("US-ASCII", "ascii-string");
		c.deserialize("ascii-string", buffer);
	}

}
