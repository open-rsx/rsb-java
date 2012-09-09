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

import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;

/**
 *
 * @author swrede
 */
public class TypeFilter extends AbstractFilter {

	private final static Logger LOG = Logger.getLogger(TypeFilter.class.getName());

	Class<?> type;

	public TypeFilter() {
		super(TypeFilter.class);
	}

	public TypeFilter(Class<?> c) {
		super(TypeFilter.class);
		type = c;
	}

	public void skip(EventId id) {
		LOG.info("Event with ID "
				+ id
				+ " will not be matched by TypeFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof TypeFilter
				&& type.equals(((TypeFilter) that).type);
	}

	@Override
	public Event transform(Event e) {
		// check skip
		EventId eventId = e.getId();
		if (mustSkip(eventId)) {
			LOG.info("event with ID " + eventId + " whitelisted in TypeFilter!");
			skipped(eventId);
			return e;
		}
		// condition: class types are equal
		if (type.isAssignableFrom(e.getClass())) {
			return e;
		} else {
			return null;
		}
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}
}
