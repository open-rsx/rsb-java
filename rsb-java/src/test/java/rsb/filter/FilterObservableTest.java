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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rsb.LoggingEnabled;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 * @author jwienke
 */
public class FilterObservableTest extends LoggingEnabled {

    private static final class TestObserver implements FilterObserver {

        public static class Call {

            private final Filter filter;
            private final FilterAction action;

            public Call(final Filter filter, final FilterAction action) {
                this.filter = filter;
                this.action = action;
            }

            // CHECKSTYLE.OFF: AvoidInlineConditionals - Generated method

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result =
                        prime
                                * result
                                + ((this.action == null) ? 0 : this.action
                                        .hashCode());
                result =
                        prime
                                * result
                                + ((this.filter == null) ? 0 : this.filter
                                        .hashCode());
                return result;
            }

            // CHECKSTYLE.ON: AvoidInlineConditionals

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (!(obj instanceof Call)) {
                    return false;
                }
                final Call other = (Call) obj;
                if (this.action != other.action) {
                    return false;
                }
                if (this.filter == null) {
                    if (other.filter != null) {
                        return false;
                    }
                } else if (!this.filter.equals(other.filter)) {
                    return false;
                }
                return true;
            }

        }

        private final List<Call> calls = new ArrayList<Call>();

        @Override
        public void notify(final Filter filter, final FilterAction action) {
            this.calls.add(new Call(filter, action));
        }

        public List<Call> getCalls() {
            return this.calls;
        }

    }

    @Test
    public void notifyObservers() {

        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();
        observable.addObserver(observer);

        final ScopeFilter filterOne = new ScopeFilter(new Scope("/blub"));
        observable.notifyObservers(filterOne, FilterAction.ADD);
        final OriginFilter filterTwo = new OriginFilter(new ParticipantId());
        observable.notifyObservers(filterTwo, FilterAction.REMOVE);

        assertEquals(2, observer.getCalls().size());
        assertEquals(new TestObserver.Call(filterOne, FilterAction.ADD),
                observer.getCalls().get(0));
        assertEquals(new TestObserver.Call(filterTwo, FilterAction.REMOVE),
                observer.getCalls().get(1));

    }

    @Test
    public void addObserver() {
        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();
        assertTrue(observable.addObserver(observer));
        assertFalse(observable.addObserver(observer));
    }

    @Test
    public void removeObserver() {

        final FilterObservable observable = new FilterObservable();
        final TestObserver observer = new TestObserver();

        observable.addObserver(observer);
        assertTrue(observable.removeObserver(observer));
        assertFalse(observable.removeObserver(observer));

        observable.notifyObservers(new ScopeFilter(new Scope("/sdfasxdfdf")),
                FilterAction.ADD);

        assertTrue(observer.getCalls().isEmpty());

    }

    @Test
    public void clear() {

        final FilterObservable observable = new FilterObservable();

        final TestObserver observerOne = new TestObserver();
        observable.addObserver(observerOne);
        final TestObserver observerTwo = new TestObserver();
        observable.addObserver(observerTwo);

        observable.clearObservers();

        observable.notifyObservers(new ScopeFilter(new Scope("/sdfasdf")),
                FilterAction.ADD);

        assertTrue(observerOne.getCalls().isEmpty());
        assertTrue(observerTwo.getCalls().isEmpty());

    }

}
