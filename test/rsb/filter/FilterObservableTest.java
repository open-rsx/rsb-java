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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 */
public class FilterObservableTest {

    private final class TestObserver implements FilterObserver {

        boolean notified_of = false;
        boolean notified_tf = false;
        boolean notified_sf = false;
        boolean notified_af = false;

        @Override
        public void notify(final OriginFilter event, final FilterAction action) {
            this.notified_of = true;
        }

        @Override
        public void notify(final TypeFilter event, final FilterAction action) {
            this.notified_tf = true;
        }

        @Override
        public void notify(final ScopeFilter event, final FilterAction action) {
            this.notified_sf = true;
        }

        @Override
        public void notify(final AbstractFilter event, final FilterAction action) {
            this.notified_af = true;
        }
    }

    /**
     * Test method for
     * {@link rsb.filter.FilterObservable#addObserver(rsb.filter.FilterObserver)}
     * .
     */
    @Test
    public void addObserver() {
        final FilterObservable fo = new FilterObservable();
        fo.addObserver(new TestObserver());
        assertTrue(fo.observers.size() == 1);
    }

    /**
     * Test method for
     * {@link rsb.filter.FilterObservable#removeObserver(rsb.filter.FilterObserver)}
     * .
     */
    @Test
    public void removeObserver() {
        final FilterObservable fo = new FilterObservable();
        final TestObserver to = new TestObserver();
        fo.addObserver(to);
        fo.removeObserver(to);
        assertTrue(fo.observers.size() == 0);
    }

    @Test
    public void notifyObservers() {
        final FilterObservable fo = new FilterObservable();
        final TestObserver to = new TestObserver();
        fo.addObserver(to);
        fo.notifyObservers(new ScopeFilter(new Scope("/blub")),
                FilterAction.ADD);
        fo.notifyObservers(new OriginFilter(new ParticipantId()),
                FilterAction.ADD);
        fo.notifyObservers(new TypeFilter(this.getClass()), FilterAction.ADD);
        assertTrue(to.notified_sf);
        assertTrue(to.notified_tf);
        assertTrue(to.notified_of);
        assertFalse(to.notified_af);
    }

    /**
     * Test method for {@link rsb.filter.FilterObservable#clearObservers()}.
     */
    @Test
    public void clearObservers() {
        final FilterObservable fo = new FilterObservable();
        final TestObserver to = new TestObserver();
        fo.addObserver(to);
        fo.clearObservers();
        assertTrue(fo.observers.size() == 0);
    }

}
