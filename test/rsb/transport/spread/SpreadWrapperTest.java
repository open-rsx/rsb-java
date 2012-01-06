/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
package rsb.transport.spread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rsb.InitializeException;

/**
 * @author swrede
 * 
 */
public class SpreadWrapperTest {

	/**
	 * Test method for
	 * {@link rsb.transport.spread.SpreadWrapper#SpreadWrapper()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSpreadWrapper() throws InitializeException {
		SpreadWrapper spread = new SpreadWrapper();
		assertNotNull(spread);
		assertEquals(SpreadWrapper.State.DEACTIVATED, spread.getStatus());
		spread.activate();
		assertEquals(SpreadWrapper.State.ACTIVATED, spread.getStatus());
	}

}
