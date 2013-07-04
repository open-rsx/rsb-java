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
import rsb.EventId;
import rsb.ParticipantId;

/**
 * Events matched by this filter have to originate from a particular
 * participant.
 * 
 * @author swrede
 * @author jmoringe
 */
public class OriginFilter extends AbstractFilter {

    ParticipantId origin;
    boolean invert = false;

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
    public Event transform(final Event e) {
        boolean matches = e.getId().getParticipantId().equals(this.origin);
        matches = this.invert ? !matches : matches;
        if (matches) {
            return e;
        } else {
            return null;
        }
    }

    /*
     * Helper method for double dispatch of Filter registrations
     */
    @Override
    public void dispachToObserver(final FilterObserver o, final FilterAction a) {
        o.notify(this, a);
    }

    @Override
    public void skip(final EventId id) {
        super.skip(id);
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof OriginFilter
                && this.origin.equals(((OriginFilter) that).origin)
                && (this.invert == ((OriginFilter) that).invert);
    }

}
