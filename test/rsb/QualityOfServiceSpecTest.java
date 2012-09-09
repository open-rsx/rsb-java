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
package rsb;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;

/**
 * @author jwienke
 */
public class QualityOfServiceSpecTest {

	@Test
	public void defaultSettings() {
		QualityOfServiceSpec spec = new QualityOfServiceSpec();
		assertEquals(QualityOfServiceSpec.Ordering.UNORDERED,
				spec.getOrdering());
		assertEquals(QualityOfServiceSpec.Reliability.RELIABLE,
				spec.getReliability());
	}

	@Test
	public void constructor() {
		final QualityOfServiceSpec.Ordering ordering = Ordering.ORDERED;
		final QualityOfServiceSpec.Reliability reliability = Reliability.UNRELIABLE;
		QualityOfServiceSpec spec = new QualityOfServiceSpec(ordering,
				reliability);
		assertEquals(ordering, spec.getOrdering());
		assertEquals(reliability, spec.getReliability());
	}

	@Test
	public void comparison() {
		assertEquals(new QualityOfServiceSpec(), new QualityOfServiceSpec());
		assertFalse(new QualityOfServiceSpec().equals(new QualityOfServiceSpec(
				Ordering.ORDERED, Reliability.RELIABLE)));
		assertFalse(new QualityOfServiceSpec().equals(new QualityOfServiceSpec(
				Ordering.UNORDERED, Reliability.UNRELIABLE)));
	}

}
