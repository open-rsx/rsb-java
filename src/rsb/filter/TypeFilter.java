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

    private final static Logger LOG = Logger.getLogger(TypeFilter.class
            .getName());

    private Class<?> type;

    public TypeFilter() {
        super(TypeFilter.class);
    }

    public TypeFilter(final Class<?> type) {
        super(TypeFilter.class);
        this.type = type;
    }

    @Override
    public void skip(final EventId eventId) {
        LOG.info("Event with ID "
                + eventId
                + " will not be matched by TypeFilter as this was already done by network layer!");
        super.skip(eventId);
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof TypeFilter
                && this.type.equals(((TypeFilter) that).type);
    }

    @Override
    public Event transform(final Event event) {
        // check skip
        final EventId eventId = event.getId();
        if (this.mustSkip(eventId)) {
            LOG.info("event with ID " + eventId + " whitelisted in TypeFilter!");
            this.skipped(eventId);
            return event;
        }
        // condition: class types are equal
        if (this.type.isAssignableFrom(event.getClass())) {
            return event;
        } else {
            return null;
        }
    }

    /**
     * Helper method for double dispatch of Filter registrations
     */
    @Override
    public void dispachToObserver(final FilterObserver observer,
            final FilterAction action) {
        observer.notify(this, action);
    }

    @Override
    public int hashCode() {
        return 31 * this.type.hashCode();
    }
}
