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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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

    MatchAndDispatchTask(final Handler handler, final Set<Filter> filters,
            final Event event,
            final Map<Handler, Set<MatchAndDispatchTask>> handlerTasks) {
        this.handler = handler;
        this.filters = filters;
        this.event = event;
        this.handlerTasks = handlerTasks;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            if (this.match(this.event)) {
                try {
                    this.handler.internalNotify(this.event);
                } catch (final Throwable ex) {
                    // TODO add logger, re-throw exception to user-specified
                    // exception handler
                }
                return true;
            }
            return false;
        } finally {
            synchronized (this.handlerTasks) {
                this.handlerTasks.get(this.handler).remove(this);
            }
            this.handlerTasks.notifyAll();
        }
    }

    public boolean match(final Event event) {
        Event result = event;
        synchronized (this.filters) {
            for (final Filter filter : this.filters) {
                result = filter.transform(result);
                if (result == null) {
                    return false;
                }
            }
        }
        return true;
    }

}
