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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.RsbTestCase;

/**
 * @author swrede
 */
public class ConverterSignatureTest extends RsbTestCase {

    private static final String ENCODING = "utf-8-string";

    @Test
    public void converterSignature() {
        final ConverterSignature sig = new ConverterSignature(ENCODING,
                String.class);
        assertTrue(sig.getSchema().contentEquals(ENCODING));
        assertTrue(sig.getDataType().getName()
                .contentEquals("java.lang.String"));
    }

    @Test
    public void equalsObject() {
        final ConverterSignature sig1 = new ConverterSignature(ENCODING,
                String.class);
        final ConverterSignature sig2 = new ConverterSignature(ENCODING,
                String.class);
        assertEquals(sig1, sig2);
    }

}
