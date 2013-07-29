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

import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 */
public class FilterObservableTest {

    private final class TestObserver implements FilterObserver {

        boolean notifiedOrigin = false;
        boolean notifiedType = false;
        boolean notifiedScope = false;
        boolean notifiedAbstract = false;

        @Override
        public void notify(final OriginFilter event, final FilterAction action) {
            this.notifiedOrigin = true;
        }

        @Override
        public void notify(final TypeFilter event, final FilterAction action) {
            this.notifiedType = true;
        }

        @Override
        public void notify(final ScopeFilter event, final FilterAction action) {
            this.notifiedScope = true;
        }

        @Override
        public void notify(final AbstractFilter event, final FilterAction action) {
            this.notifiedAbstract = true;
        }
    }

    /**
     * Test method for
     * {@link rsb.filter.FilterObservable#addObserver(rsb.filter.FilterObserver)}
     * .
     */
    @Test
    public void addObserver() {
        final FilterObservable observable = new FilterObservable();
        observable.addObserver(new TestObserver());
        assertEquals(1, observable.observers.size());
    }

    /**
     * Test method for
     * {@link rsb.filter.FilterObservable#removeObserver(rsb.filter.FilterObserver)}
     * .
     */
    @Test
    public void removeObserver() {
        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();
        observable.addObserver(observer);
        observable.removeObserver(observer);
        assertEquals(0, observable.observers.size());
    }

    @Test
    public void notifyObservers() {
        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();
        observable.addObserver(observer);
        observable.notifyObservers(new ScopeFilter(new Scope("/blub")),
                FilterAction.ADD);
        observable.notifyObservers(new OriginFilter(new ParticipantId()),
                FilterAction.ADD);
        observable.notifyObservers(new TypeFilter(this.getClass()),
                FilterAction.ADD);
        assertTrue(observer.notifiedScope);
        assertTrue(observer.notifiedType);
        assertTrue(observer.notifiedOrigin);
        assertFalse(observer.notifiedAbstract);
    }

    /**
     * Test method for {@link rsb.filter.FilterObservable#clearObservers()}.
     */
    @Test
    public void clearObservers() {
        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();
        observable.addObserver(observer);
        observable.clearObservers();
        assertEquals(0, observable.observers.size());
    }

}
