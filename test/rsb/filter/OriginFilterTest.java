/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 Jan Moringen
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
package rsb.filter;

import rsb.Scope;
import rsb.ParticipantId;
import rsb.Event;

import rsb.filter.OriginFilter;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for the {@link rsb.filter.OriginFilter} class.
 *
 * @author jmoringe
 */
public class OriginFilterTest {
        @Test
        public void testTransform() {
                ParticipantId origin1 = new ParticipantId();
                Event e1 = new Event(new Scope("/images"), String.class, "bla");
		e1.setId(origin1, 234);

                ParticipantId origin2 = new ParticipantId();
                Event e2 = new Event(new Scope("/images"), String.class, "bla");
		e2.setId(origin2, 0);

		OriginFilter f1 = new OriginFilter(origin1);
		assertTrue(f1.transform(e1) != null);
                assertTrue(f1.transform(e2) == null);

                OriginFilter f2 = new OriginFilter(origin1, true);
		assertTrue(f2.transform(e1) == null);
                assertTrue(f2.transform(e2) != null);
        }
};
