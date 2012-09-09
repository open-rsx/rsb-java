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

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ScopeFilterTest {

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#transform(rsb.Event)}.
	 */
	@Test
	public void testTransform() {
		Event e = new Event();
		e.setScope(new Scope("/images"));
		e.setId(new ParticipantId(), 234);
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertTrue(sf.transform(e) != null);
		e.setScope(new Scope("/nomatch"));
		assertTrue(sf.transform(e) == null);
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#skip(rsb.event.EventId)}.
	 */
	@Test
	public void testSkipEventId() {
		Event e = new Event();
		// TODO actually, we need a mock object to test this correctly
		// setting the Scope to another name than the scope filters
		// configuration is just to check here whether the white-
		// listing really works
		e.setScope(new Scope("/images/justfortesting"));
		e.setId(new ParticipantId(), 43543);
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		sf.skip(e.getId());
		assertTrue(sf.transform(e) != null);
	}

	/**
	 * Test method for
	 * {@link rsb.filter.ScopeFilter#ScopeFilter(java.lang.String)}.
	 */
	@Test
	public void testScopeFilter() {
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertNotNull(sf);
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#getScope()}.
	 */
	@Test
	public void testGetScope() {
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertTrue(sf.getScope().equals(new Scope("/images")));
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#equals(rsb.filter.Filter)}.
	 */
	@Test
	public void testEqualsFilter() {
		ScopeFilter sf1 = new ScopeFilter(new Scope("/images"));
		ScopeFilter sf2 = new ScopeFilter(new Scope("/images"));
		assertTrue(sf1.equals(sf2));
		sf2.setScope(new Scope("/nope"));
		assertFalse(sf1.equals(sf2));
	}

}
