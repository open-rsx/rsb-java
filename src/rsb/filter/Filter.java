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

import rsb.Event;

/**
 * @author swrede
 */
public interface Filter {

	// public boolean isStateless();

	/**
	 * transform the given event into a result event according to this filters
	 * rules. This may return null, if the event is dropped by the filter, the
	 * passed-in event if no transformation is applied, or some new event.
	 *
	 * @param e
	 *            the event to be transformed
	 * @return the transformed event or null
	 */
	public Event transform(Event e);

	// /**
	// * tell this MTF to skip any event matching the specified ID it
	// encounters.
	// * This causes the MTF to behave like an identity transformation and just
	// * return the event as-is when it's passed to transform().
	// *
	// * @param id
	// * the event ID to be skipped
	// * @see #transform(Event)
	// */
	// public void skip(EventId id);

	public boolean equals(Object that);

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a);

}
