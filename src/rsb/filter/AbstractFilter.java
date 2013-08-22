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

    private static final Logger LOG = Logger.getLogger(AbstractFilter.class
            .getName());

    /** Stores whitelisted event ids registered by {@link #skip(EventId)}. */
    protected Set<EventId> whitelist = new HashSet<EventId>();

    /** Stores the type info for this filter. */
    protected String type = AbstractFilter.class.getSimpleName();

    /**
     * Constructor.
     *
     * @param type
     *            type of this filter
     */
    protected AbstractFilter(final String type) {
        this.type = type;
    }

    public AbstractFilter(final Class<? extends AbstractFilter> type) {
        this.type = type.getSimpleName();
    }

    @Override
    public abstract Event transform(Event event);

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
     * Returns whether events with the specified ID should be skipped or not.
     *
     * @param eventId
     *            id to skip
     * @return <code>true</code>, if the event with the specified ID should be
     *         skipped
     */
    public boolean mustSkip(final EventId eventId) {
        return this.whitelist.contains(eventId);
    }

    /**
     * Remove ID from the list after the corresponding event has been skipped.
     *
     * @param eventId
     *            id to remove
     */
    public void skipped(final EventId eventId) {
        this.whitelist.remove(eventId);
    }

    @Override
    public abstract void dispachToObserver(FilterObserver observer,
            FilterAction action);

}
