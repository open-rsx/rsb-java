/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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

import java.util.LinkedList;

import org.junit.Test;

import rsb.converter.ConversionException;
import rsb.converter.BoolConverter;
import rsb.converter.WireContents;

/**
 * @author jmoringe
 */
public class BoolConverterTest {

    @Test
    public void serialize() throws Throwable {
        BoolConverter c = new BoolConverter();
        Boolean b = true;
        WireContents<ByteBuffer> buf = c.serialize(Boolean.class, b);
        assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
        BoolConverter c = new BoolConverter();
        {
            Boolean b1 = false;
            WireContents<ByteBuffer> buf = c.serialize(Boolean.class, b1);
            assertNotNull(buf);
            Object o
                = c.deserialize(buf.getWireSchema(), buf.getSerialization())
                .getData();
            Boolean b2 = (Boolean) o;
            assertEquals(b1, b2);
        }

        {
            Boolean b1 = true;
            WireContents<ByteBuffer> buf = c.serialize(Boolean.class, b1);
            assertNotNull(buf);
            Object o
                = c.deserialize(buf.getWireSchema(), buf.getSerialization())
                .getData();
            Boolean b2 = (Boolean) o;
            assertEquals(b1, b2);
        }
    }

    @Test(expected = ConversionException.class)
    public void serializationNotABoolError() throws Throwable {
        BoolConverter c = new BoolConverter();
        c.serialize(Boolean.class, new LinkedList<Integer>());
    }

}
