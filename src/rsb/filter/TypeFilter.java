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

import rsb.Event;

/**
 * A {@link Filter} that only accepts events which have a payload of a specific
 * type. Events are accepted if their payload can be assigned to the desired
 * class (ie. also subclasses are accepted).
 *
 * @author swrede
 * @author jwienke
 */
public class TypeFilter implements Filter {

    private final Class<?> type;

    /**
     * Constructor.
     *
     * @param type
     *            the desired payload type, not <code>null</code>
     * @throws IllegalArgumentException
     *             type is <code>null</code>
     */
    public TypeFilter(final Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.type = type;
    }

    @Override
    public boolean match(final Event event) {
        return this.type.isAssignableFrom(event.getType());
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof TypeFilter
                && this.type.equals(((TypeFilter) that).type);
    }

    @Override
    public int hashCode() {
        return 31 * this.type.hashCode();
    }
}
