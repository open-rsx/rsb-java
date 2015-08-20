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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.Event;
import rsb.LoggingEnabled;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 */
public class ScopeFilterTest extends LoggingEnabled {

    private static final Scope DEFAULT_SCOPE = new Scope("/images");
    private static final int RANDOM_SEQUENCE = 2345;

    /**
     * Test method for {@link rsb.filter.ScopeFilter#match(rsb.Event)}.
     */
    @Test
    public void match() {
        final Event event = new Event();
        event.setScope(DEFAULT_SCOPE);
        event.setId(new ParticipantId(), RANDOM_SEQUENCE);
        final ScopeFilter filter = new ScopeFilter(DEFAULT_SCOPE);
        assertTrue(filter.match(event));
        event.setScope(new Scope("/nomatch"));
        assertFalse(filter.match(event));
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
        assertEquals(new ScopeFilter(DEFAULT_SCOPE), new ScopeFilter(
                DEFAULT_SCOPE));
        assertFalse(new ScopeFilter(DEFAULT_SCOPE).equals(new ScopeFilter(
                new Scope("/foooo"))));
    }

}
