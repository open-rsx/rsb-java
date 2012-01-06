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
package rsb.eventprocessing;

import rsb.Handler;
import rsb.filter.Filter;
import rsb.transport.EventHandler;

/**
 * An interface for strategies that dispatch received events to {@link Handler}
 * s. Implementations have the task of freeing the caller of
 * {@link #handle(rsb.Event)} to dispatch the event in its thread.
 * 
 * @author jwienke
 */
public interface EventReceivingStrategy extends EventHandler {

	/**
	 * Adds a filter that is applied for all registered handlers some time after
	 * this method call.
	 * 
	 * @param filter
	 *            filter to add, not <code>null</code>
	 */
	void addFilter(Filter filter);

	/**
	 * Removes a filter that is remove for all registered handlers some time
	 * after this method call.
	 * 
	 * @param filter
	 *            filter to remove, not <code>null</code>
	 */
	void removeFilter(Filter filter);

	void addHandler(Handler handler, boolean wait);

	void removeHandler(Handler handler, boolean wait)
			throws InterruptedException;

	void shutdownAndWait() throws InterruptedException;

}
