/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class FilterObservable {
	
	private final static Logger LOG = Logger.getLogger(FilterObservable.class.getName());

	List<FilterObserver> observers = new ArrayList<FilterObserver>();
	
	public void addObserver(final FilterObserver observer) {
		LOG.finest("Added observer" + observer);
		observers.add(observer);
	}
	
	public void removeObserver(final FilterObserver observer) {
		LOG.finest("Removed observer" + observer);
		observers.remove(observer);
	}
	
	public void notifyObservers(final Filter filter, final FilterAction action) {
		for (FilterObserver observer : observers) {
			// perform double dispatch
			filter.dispachToObserver(observer, action);
		}
	}
	
	public void clearObservers() {
		observers.clear();
	}
}
