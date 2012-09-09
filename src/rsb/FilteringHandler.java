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
package rsb;

import java.util.HashSet;
import java.util.Set;

import rsb.filter.Filter;

/**
 * A decorator for {@link Handler}s that allows additional filtering for each
 * handler. Filters are a conjunction, hence one failure prevents delivery.
 *
 * @author jwienke
 */
public class FilteringHandler implements Handler {

	private Handler decorated;
	private Set<Filter> filters = new HashSet<Filter>();

	/**
	 * Creates a filtering handler that wraps another handler and takes one
	 * additional filter.
	 *
	 * @param decorated
	 *            handler to wrap and call if filter matches
	 * @param filter
	 *            the additional filter to apply
	 */
	public FilteringHandler(Handler decorated, Filter filter) {
		this.decorated = decorated;
		this.filters.add(filter);
	}

	/**
	 * Creates a filtering handler that wraps another handler and takes a set of
	 * filters to apply.
	 *
	 * @param decorated
	 *            handler to wrap and call if filter matches
	 * @param filters
	 *            the additional filters to apply
	 */
	public FilteringHandler(Handler decorated, Set<Filter> filters) {
		this.decorated = decorated;
		this.filters.addAll(filters);
	}

	@Override
	public void internalNotify(Event e) {
		for (Filter f : filters) {
			if (f.transform(e) == null) {
				return;
			}
		}
		decorated.internalNotify(e);
	}

}
