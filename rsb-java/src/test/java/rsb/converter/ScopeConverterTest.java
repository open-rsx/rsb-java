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

import rsb.Scope;
import rsb.RsbTestCase;

/**
 * @author jmoringe
 */
public class ScopeConverterTest extends RsbTestCase {

    @Test
    public void serialize() throws Throwable {
        final ScopeConverter converter = new ScopeConverter();
        final Scope input = new Scope("/test");
        final WireContents<ByteBuffer> buf =
                converter.serialize(Scope.class, input);
        assertNotNull(buf);
    }

    @Test
    public void roundtrip() throws Throwable {
        final ScopeConverter converter = new ScopeConverter();
        final Scope input = new Scope("/test/roundtrip");
        final WireContents<ByteBuffer> buf =
                converter.serialize(Scope.class, input);
        assertNotNull(buf);
        final Object output =
                converter.deserialize(buf.getWireSchema(),
                        buf.getSerialization()).getData();
        final Scope outputScope = (Scope) output;
        assertEquals(input, outputScope);
    }

    @Test(expected = ConversionException.class)
    public void serializationNotAStringError() throws Throwable {
        final ScopeConverter converter = new ScopeConverter();
        converter.serialize(Scope.class, new LinkedList<Integer>());
    }

}
