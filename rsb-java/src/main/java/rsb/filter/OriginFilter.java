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
public class OriginFilter implements Filter {

    private final ParticipantId origin;
    private final boolean inverted;

    /**
     * Constructor.
     *
     * @param origin
     *            the desired origin of a received event, not <code>null</code>
     * @param invert
     *            if <code>true</code>, suppress events from the provided origin
     *            and allow all other events instead of only allowing events
     *            from that origin.
     * @throws IllegalArgumentException
     *             origin is <code>null</code>
     */
    public OriginFilter(final ParticipantId origin, final boolean invert) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin must not be null");
        }
        this.origin = origin;
        this.inverted = invert;
    }

    /**
     * Constructor.
     *
     * @param origin
     *            only events from this origin are allowed, not
     *            <code>null</code>
     * @throws IllegalArgumentException
     *             origin is <code>null</code>
     */
    public OriginFilter(final ParticipantId origin) {
        this(origin, false);
    }

    /**
     * Returns the origin participant id this filter operates for.
     *
     * @return the id, not <code>null</code>
     */
    public ParticipantId getOrigin() {
        return this.origin;
    }

    /**
     * Indicates whether the filter accepts events only from the id returned by
     * {@link #getOrigin()} or it only accepts events that do not originate from
     * that origin.
     *
     * @return if <code>true</code>, only events NOT originating from
     *         {@link #getOrigin()} are allowed. If <code>false</code>, only
     *         events from {@link #getOrigin()} are allowed.
     */
    public boolean isInverted() {
        return this.inverted;
    }

    @Override
    public boolean match(final Event event) {
        final boolean matches =
                event.getId().getParticipantId().equals(this.origin);
        if (this.inverted) {
            return !matches;
        } else {
            return matches;
        }
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof OriginFilter
                && this.origin.equals(((OriginFilter) that).origin)
                && this.inverted == ((OriginFilter) that).inverted;
    }

    @Override
    public int hashCode() {
        return 31 * this.origin.hashCode() + 7
                * Boolean.valueOf(this.inverted).hashCode();
    }

}
