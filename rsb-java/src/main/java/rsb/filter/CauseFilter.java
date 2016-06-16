/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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

/**
 * Filter events based on cause.
 */
public class CauseFilter implements Filter {

    private final EventId cause;
    private final boolean inverted;

    /**
     * Construct a cause filter.
     *
     * @param cause the cause
     * @param invert invert he filter?
     */
    public CauseFilter(final EventId cause, final boolean invert) {
        this.cause = cause;
        this.inverted = invert;
    }

    /**
     * Construct a cause filter.
     *
     * @param cause the cause
     */
    public CauseFilter(final EventId cause) {
        this(cause, false);
    }

    /**
     * Indicates whether the filter accepts events containing the
     * event id returned by {@link #getCause()} in their cause vector
     * or events that do not have that cause.
     *
     * @return if <code>true</code>, only events NOT that do not have
     *         {@link #getCause()} in their cause vector are
     *         allowed. If <code>false</code>, only events that have
     *         {@link #getCause()} in their cause vector are allowed.
     */
    public boolean isInverted() {
        return this.inverted;
    }

    @Override
    public boolean match(final Event event) {
        final boolean matches = event.isCause(this.cause);
        if (this.inverted) {
            return !matches;
        } else {
            return matches;
        }
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof CauseFilter
                && this.cause.equals(((CauseFilter) that).cause)
                && this.inverted == ((CauseFilter) that).inverted;
    }

    @Override
    public int hashCode() {
        return 31 * this.cause.hashCode() + 7
                * Boolean.valueOf(this.inverted).hashCode();
    }

}
