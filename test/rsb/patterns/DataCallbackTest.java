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
package rsb.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import rsb.Event;

/**
 * @author jwienke
 */
public class DataCallbackTest {

    @Test
    public void invokeVoid() throws Throwable {

        class VoidCallback extends DataCallback<Void, Void> {

            @Override
            public Void invoke(final Void request) throws Throwable {
                return null;
            }

        }

        final Event result = new VoidCallback().internalInvoke(new Event(
                Void.class, null));
        assertEquals(Void.class, result.getType());
        assertNull(result.getData());

    }

    @Test
    public void invoke() throws Throwable {

        final String testString = "blaaaa";

        class MyCallback extends DataCallback<String, Integer> {

            @Override
            public String invoke(final Integer request) throws Throwable {
                return testString;
            }

        }

        final Event result = new MyCallback().internalInvoke(new Event(
                Integer.class, 42));
        assertEquals(String.class, result.getType());
        assertEquals(testString, result.getData());

    }

}
