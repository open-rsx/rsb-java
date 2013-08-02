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
 * Filters for a specific method in the event.
 *
 * @author jwienke
 */
public class MethodFilter extends AbstractFilter {

    // private final static Logger LOG =
    // Logger.getLogger(MethodFilter.class.getName());

    private final String method;

    /**
     * Constructor.
     *
     * @param method
     *            method to allow
     */
    public MethodFilter(final String method) {
        super(MethodFilter.class);
        this.method = method;
    }

    @Override
    public void dispachToObserver(final FilterObserver observer,
            final FilterAction action) {
        observer.notify(this, action);

    }

    @Override
    public Event transform(final Event event) {
        if (event.getMethod() != null
                && event.getMethod().equalsIgnoreCase(this.method)) {

            return event;
        }
        return null;
    }

}
