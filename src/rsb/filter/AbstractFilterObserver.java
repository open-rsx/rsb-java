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

/**
 * @author swrede
 * 
 */
public class AbstractFilterObserver implements FilterObserver {

    protected final static Logger LOG = Logger
            .getLogger(AbstractFilterObserver.class.getCanonicalName());

    /*
     * (non-Javadoc)
     * 
     * @see rsb.filter.FilterObserver#notify(rsb.filter.AbstractFilter,
     * rsb.filter.FilterAction)
     */
    @Override
    public void notify(final AbstractFilter event, final FilterAction action) {
        LOG.fine("AbstractFilterObser::notify(AbstractFilter e, FilterAction a) called");
    }

    /*
     * (non-Javadoc)
     * 
     * @see rsb.filter.FilterObserver#notify(rsb.filter.ScopeFilter,
     * rsb.filter.FilterAction)
     */
    @Override
    public void notify(final ScopeFilter event, final FilterAction action) {
        LOG.fine("AbstractFilterObser::notify(ScopeFilter e, FilterAction a) called");
    }

    /*
     * (non-Javadoc)
     * 
     * @see rsb.filter.FilterObserver#notify(rsb.filter.TypeFilter,
     * rsb.filter.FilterAction)
     */
    @Override
    public void notify(final TypeFilter event, final FilterAction action) {
        LOG.fine("AbstractFilterObser::notify(TypeFilter e, FilterAction a) called");

    }

    /*
     * (non-Javadoc)
     * 
     * @see rsb.filter.FilterObserver#notify(rsb.filter.OriginFilter,
     * rsb.filter.FilterAction)
     */
    @Override
    public void notify(final OriginFilter event, final FilterAction action) {
        LOG.fine("IdentityFilterObser::notify(TypeFilter e, FilterAction a) called");
    }

}
