/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2012 Jan Moringen
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * Unit tests for the {@link rsb.filter.OriginFilter} class.
 * 
 * @author jmoringe
 */
public class OriginFilterTest {

    @Test
    public void transform() {
        final ParticipantId origin1 = new ParticipantId();
        final Event e1 = new Event(new Scope("/images"), String.class, "bla");
        e1.setId(origin1, 234);

        final ParticipantId origin2 = new ParticipantId();
        final Event e2 = new Event(new Scope("/images"), String.class, "bla");
        e2.setId(origin2, 0);

        final OriginFilter f1 = new OriginFilter(origin1);
        assertTrue(f1.transform(e1) != null);
        assertTrue(f1.transform(e2) == null);

        final OriginFilter f2 = new OriginFilter(origin1, true);
        assertTrue(f2.transform(e1) == null);
        assertTrue(f2.transform(e2) != null);
    }

};
