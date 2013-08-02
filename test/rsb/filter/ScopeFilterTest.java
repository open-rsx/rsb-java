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
package rsb.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 */
public class ScopeFilterTest {

    private static final Scope DEFAULT_SCOPE = new Scope("/images");
    private static final int RANDOM_SEQUENCE = 2345;

    /**
     * Test method for {@link rsb.filter.ScopeFilter#transform(rsb.Event)}.
     */
    @Test
    public void transform() {
        final Event event = new Event();
        event.setScope(DEFAULT_SCOPE);
        event.setId(new ParticipantId(), RANDOM_SEQUENCE);
        final ScopeFilter filter = new ScopeFilter(DEFAULT_SCOPE);
        assertNotNull(filter.transform(event));
        event.setScope(new Scope("/nomatch"));
        assertNull(filter.transform(event));
    }

    @Test
    public void skipEventId() {
        final Event event = new Event();
        // TODO actually, we need a mock object to test this correctly
        // setting the Scope to another name than the scope filters
        // configuration is just to check here whether the white-
        // listing really works
        event.setScope(DEFAULT_SCOPE.concat(new Scope("/justfortesting")));
        event.setId(new ParticipantId(), RANDOM_SEQUENCE);
        final ScopeFilter filter = new ScopeFilter(DEFAULT_SCOPE);
        filter.skip(event.getId());
        assertNotNull(filter.transform(event));
    }

    /**
     * Test method for {@link rsb.filter.ScopeFilter#getScope()}.
     */
    @Test
    public void getScope() {
        final ScopeFilter filter = new ScopeFilter(DEFAULT_SCOPE);
        assertEquals(DEFAULT_SCOPE, filter.getScope());
    }

    @Test
    public void equalsFilter() {
        final ScopeFilter filter1 = new ScopeFilter(DEFAULT_SCOPE);
        final ScopeFilter filter2 = new ScopeFilter(DEFAULT_SCOPE);
        assertEquals(filter1, filter2);
        filter2.setScope(new Scope("/nope"));
        assertFalse(filter1.equals(filter2));
    }

}
