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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author swrede
 * @deprecated This class will be removed because the double dispatch logic has
 *             been removed from {@link FilterObserver}.
 */
@Deprecated
public class AbstractFilterObserver implements FilterObserver {

    private static final Logger LOG = Logger
            .getLogger(AbstractFilterObserver.class.getCanonicalName());

    // CHECKSTYLE.OFF: JavadocMethod - deprecated type, will be removed
    // CHECKSTYLE.OFF: MultipleStringLiterals - deprecated type, will be removed

    protected void
            notify(final AbstractFilter event, final FilterAction action) {
        LOG.log(Level.FINE,
                "AbstractFilterObserver::notify(AbstractFilter {0}, "
                        + "FilterAction {1}) called", new Object[] { event,
                        action });
    }

    protected void notify(final ScopeFilter event, final FilterAction action) {
        LOG.log(Level.FINE, "AbstractFilterObserver::notify(ScopeFilter {0}, "
                + "FilterAction {1}) called", new Object[] { event, action });
    }

    protected void notify(final TypeFilter event, final FilterAction action) {
        LOG.log(Level.FINE, "AbstractFilterObserver::notify(TypeFilter {0}, "
                + "FilterAction {1}) called", new Object[] { event, action });

    }

    protected void notify(final OriginFilter event, final FilterAction action) {
        LOG.log(Level.FINE, "AbstractFilterObserver::notify(OriginFilter {0}, "
                + "FilterAction {1}) called", new Object[] { event, action });
    }

    // CHECKSTYLE.ON: MultipleStringLiterals
    // CHECKSTYLE.ON: JavadocMethod

    @Override
    public void notify(final Filter filter, final FilterAction action) {
        if (filter instanceof ScopeFilter) {
            notify((ScopeFilter) filter, action);
        } else if (filter instanceof TypeFilter) {
            notify((TypeFilter) filter, action);
        } else if (filter instanceof OriginFilter) {
            notify((OriginFilter) filter, action);
        } else if (filter instanceof AbstractFilter) {
            notify((AbstractFilter) filter, action);
        } else {
            LOG.log(Level.WARNING, "Unsupported filter type {0}",
                    filter.getClass());
        }
    }

}
