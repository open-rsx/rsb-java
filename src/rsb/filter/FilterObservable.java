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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utility class maintaining a list of {@link FilterObserver} instances and the
 * required methods to notify these instances about filter changes. This class
 * is not thread-safe.
 *
 * @author swrede
 * @author jwienke
 */
public class FilterObservable {

    private static final Logger LOG = Logger.getLogger(FilterObservable.class
            .getName());

    private final Set<FilterObserver> observers = new HashSet<FilterObserver>();

    /**
     * If not already registered, adds this observer to the list of registered
     * observers.
     *
     * @param observer
     *            observer to add
     * @return <code>true</code> if the observer was actually newly registered.
     */
    public boolean addObserver(final FilterObserver observer) {
        LOG.finest("Added observer" + observer);
        return this.observers.add(observer);
    }

    /**
     * Removes a potentially registered observer.
     *
     * @param observer
     *            the observer to remove
     * @return <code>true</code> if the observer was previously registered and
     *         is now unregistered
     */
    public boolean removeObserver(final FilterObserver observer) {
        LOG.finest("Removed observer" + observer);
        return this.observers.remove(observer);
    }

    /**
     * Notifies all registered observers about a change to a {@link Filter}.
     *
     * @param filter
     *            the filter that is affected, not <code>null</code>
     * @param action
     *            the action performed to the filter, not <code>null</code>
     */
    public void notifyObservers(final Filter filter, final FilterAction action) {
        for (final FilterObserver observer : this.observers) {
            observer.notify(filter, action);
        }
    }

    /**
     * Removes all registered observers.
     */
    public void clearObservers() {
        this.observers.clear();
    }

}
