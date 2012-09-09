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
package rsb.eventprocessing;

import java.util.concurrent.Callable;
import java.util.Map;
import java.util.Set;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * @author swrede
 */
public class MatchAndDispatchTask implements Callable<Boolean> {

	private final Handler handler;
	private final Set<Filter> filters;
	private final Event event;
	private final Map<Handler, Set<MatchAndDispatchTask>> handlerTasks;

	MatchAndDispatchTask(Handler handler, Set<Filter> filters, Event event,
			Map<Handler, Set<MatchAndDispatchTask>> handlerTasks) {
		this.handler = handler;
		this.filters = filters;
		this.event = event;
		this.handlerTasks = handlerTasks;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			if (match(event)) {
				try {
					handler.internalNotify(event);
				} catch (Throwable ex) {
					// TODO add logger, re-throw exception to user-specified
					// exception handler
					ex.printStackTrace();
				}
				return true;
			}
			return false;
		} finally {
			synchronized (handlerTasks) {
				handlerTasks.get(handler).remove(this);
			}
			handlerTasks.notifyAll();
		}
	}

	public boolean match(Event event) {
		Event result = event;
		synchronized (filters) {
			for (Filter filter : filters) {
				result = filter.transform(result);
				if (result == null) {
					return false;
				}
			}
		}
		return true;
	}

}
