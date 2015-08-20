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
 * Interfaces for classes that indicate whether a received {@link Event} shall
 * be dispatched or not.
 *
 * Filters must be immutable because parameters updates at runtime are not
 * supported.
 *
 * @author jwienke
 * @author swrede
 */
public interface Filter {

    /**
     * Tells whether the given event matches the filter and hence shall be
     * dispatched or not.
     *
     * @param event
     *            the event to test, not <code>null</code>
     * @return <code>true</code> if the event matches the restrictions specified
     *         by this filter and hence can be delivered to the client,
     *         <code>false</code> to remove the event from the stream.
     */
    boolean match(Event event);

}
