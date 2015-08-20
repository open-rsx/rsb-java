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
import rsb.Scope;

/**
 * A filter that only accepts events from a scope and all its subscopes.
 *
 * @author swrede
 * @author jwienke
 */
public class ScopeFilter implements Filter {

    private final Scope scope;

    /**
     * Constructor.
     *
     * @param scope
     *            the desired scope, not <code>null</code>
     * @throws IllegalArgumentException
     *             scope is <code>null</code>
     */
    public ScopeFilter(final Scope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("Scope must not be null");
        }
        this.scope = scope;
    }

    /**
     * Returns the scope this filter operates for. Events from this scope and
     * all subscopes are accepted.
     *
     * @return the scope, not <code>null</code>
     */
    public Scope getScope() {
        return this.scope;
    }

    @Override
    public boolean match(final Event event) {
        return this.scope.equals(event.getScope())
                || this.scope.isSuperScopeOf(event.getScope());
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof ScopeFilter
                && this.scope.equals(((ScopeFilter) that).getScope());
    }

    @Override
    public int hashCode() {
        return 31 * this.scope.hashCode();
    }

}
