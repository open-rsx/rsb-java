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
import rsb.Scope;

/**
 * @author swrede
 */
public class ScopeFilter extends AbstractFilter {

    private static final Logger LOG = Logger.getLogger(ScopeFilter.class
            .getName());

    private Scope scope;

    public ScopeFilter(final Scope scope) {
        super(ScopeFilter.class);
        this.scope = scope;
    }

    @Override
    public void dispachToObserver(final FilterObserver observer,
            final FilterAction action) {
        observer.notify(this, action);
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return this.scope;
    }

    @Override
    public void skip(final EventId eventId) {
        LOG.info("Event with ID " + eventId
                + " will not be matched by ScopeFilter "
                + "as this was already done by network layer!");
        super.skip(eventId);
    }

    @Override
    public boolean equals(final Object that) {
        return that instanceof ScopeFilter
                && this.scope.equals(((ScopeFilter) that).getScope());
    }

    @Override
    public Event transform(final Event event) {
        LOG.fine("ScopeFilter with scope " + this.scope
                + " received event to transform.");
        if (event.getScope() != null) {
            LOG.fine("  Event's receiver Scope = " + event.getScope());
        }
        boolean matches = false;
        if (this.mustSkip(event.getId())) {
            LOG.fine("event with ID " + event.getId()
                    + " whitelisted in ScopeFilter!");
            matches = true;
            this.skipped(event.getId());
        } else {
            matches =
                    this.scope.equals(event.getScope())
                            || this.scope.isSuperScopeOf(event.getScope());
        }
        if (matches) {
            LOG.fine("ScopeFilter matched successfully!");
        } else {
            LOG.fine("ScopeFilter rejected event!");
        }
        return matches ? event : null;
    }

    @Override
    public int hashCode() {
        return 31 * this.scope.hashCode();
    }

}
