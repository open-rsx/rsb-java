/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2012 Jan Moringen
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
import rsb.ParticipantId;

/**
 * Events matched by this filter have to originate from a particular
 * participant.
 *
 * @author swrede
 * @author jmoringe
 */
public class OriginFilter extends AbstractFilter {

    private final ParticipantId origin;
    private boolean invert = false;

    public OriginFilter(final ParticipantId origin, final boolean invert) {
        super(OriginFilter.class);
        this.origin = origin;
        this.invert = invert;
    }

    public OriginFilter(final ParticipantId origin) {
        this(origin, false);
    }

    public ParticipantId getOrigin() {
        return this.origin;
    }

    public boolean isInverted() {
        return this.invert;
    }

    @Override
    public Event transform(final Event event) {
        boolean matches = event.getId().getParticipantId().equals(this.origin);
        matches = this.invert ? !matches : matches;
        if (matches) {
            return event;
        } else {
            return null;
        }
    }

    /*
     * Helper method for double dispatch of Filter registrations
     */
    @Override
    public void dispachToObserver(final FilterObserver observer,
            final FilterAction action) {
        observer.notify(this, action);
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof OriginFilter
                && this.origin.equals(((OriginFilter) that).origin)
                && this.invert == ((OriginFilter) that).invert;
    }

    @Override
    public int hashCode() {
        return 31 * this.origin.hashCode() + 7
                * Boolean.valueOf(this.invert).hashCode();
    }

}
