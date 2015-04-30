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
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * @author swrede
 */
public class MatchAndDispatchTask implements Callable<Boolean> {

    private static final Logger LOG = Logger
            .getLogger(MatchAndDispatchTask.class.getName());

    private final Handler handler;
    private final Set<Filter> filters;
    private final Event event;
    private final Map<Handler, Set<MatchAndDispatchTask>> handlerTasks;

    /**
     * Constructor.
     *
     * @param handler
     *            handler to dispatch a received event to
     * @param filters
     *            filter to apply before dispatching
     * @param event
     *            the event to filter and eventually dispatch
     * @param handlerTasks
     *            internal map of handlers and their associated tasks to remove
     *            this instance from once completed
     */
    MatchAndDispatchTask(final Handler handler, final Set<Filter> filters,
            final Event event,
            final Map<Handler, Set<MatchAndDispatchTask>> handlerTasks) {
        this.handler = handler;
        this.filters = filters;
        this.event = event;
        this.handlerTasks = handlerTasks;
    }

    @Override
    public Boolean call() {
        try {
            if (this.match(this.event)) {
                try {
                    this.handler.internalNotify(this.event);
                } catch (final Exception ex) {
                    LOG.log(Level.WARNING,
                            "Unable to dispatch event to handler"
                                    + this.handler, ex);
                    // TODO pass to exception handler
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

    private boolean match(final Event event) {
        synchronized (this.filters) {
            for (final Filter filter : this.filters) {
                final boolean matches = filter.match(event);
                if (!matches) {
                    return false;
                }
            }
        }
        return true;
    }

}
