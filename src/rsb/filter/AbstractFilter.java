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

import rsb.Event;
import rsb.EventId;

/**
 * @author swrede
 */
public abstract class AbstractFilter implements Filter {

    private final static Logger LOG = Logger.getLogger(AbstractFilter.class
            .getName());

    /* stores whitelisted event ids registered by skip() */
    protected Set<EventId> whitelist = new HashSet<EventId>();

    /* stores type info */
    protected String type = AbstractFilter.class.getSimpleName();

    /**
     * This method does the actual filtering step. If an event can be matched
     * successfully against the condition of a specific filter, it is returned
     * and processed by further filtering steps. If null is returned, the event
     * is discarded.
     */
    @Override
    public abstract Event transform(Event event);

    protected AbstractFilter(final String type) {
        this.type = type;
    }

    public AbstractFilter(final Class<? extends AbstractFilter> type) {
        this.type = type.getSimpleName();
    }

    /**
     * Skip this filter for any event with the specified ID. This will cause the
     * MTF to successfully transform the event without applying any checks.
     *
     * @param eventId
     *            the id to skip
     */
    public void skip(final EventId eventId) {
        LOG.info("Event with ID " + eventId + " will not be matched by "
                + this.type + " as this was already done by network layer!");
        this.whitelist.add(eventId);
    }

    /**
     * returns whether events with the specified ID should be skipped or not.
     *
     * @param eventId
     * @return true, if the event with the specified ID should be skipped
     */
    public boolean mustSkip(final EventId eventId) {
        return this.whitelist.contains(eventId);
    }

    /**
     * remove ID from the list after the corresponding event has been skipped.
     *
     * @param eventId
     */
    public void skipped(final EventId eventId) {
        this.whitelist.remove(eventId);
    }

    /**
     * Helper method for double dispatch of Filter registrations
     */
    @Override
    public abstract void dispachToObserver(FilterObserver observer,
            FilterAction action);

}
