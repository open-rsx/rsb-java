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
package rsb;

import java.util.logging.Logger;

/**
 * Interface for State-pattern in the Listener class. Currently, just used for
 * explicit de-/allocation of resources.
 *
 * @author swrede
 */
public class ListenerState {

    private static final Logger LOG = Logger.getLogger(InformerState.class
            .getName());

    private final Listener listener;

    /**
     * Constructor.
     *
     * @param listener
     *            the listener being managed
     */
    protected ListenerState(final Listener listener) {
        this.listener = listener;
    }

    /**
     * Delegate for {@link Listener#activate()}.
     *
     * @throws RSBException
     *             error activating
     * @see Listener#activate()
     */
    protected void activate() throws RSBException {
        LOG.warning("invalid state exception during activate call");
        throw new IllegalStateException("subscriber already activated");
    }

    /**
     * Delegate for {@link Listener#deactivate()}.
     *
     * @throws RSBException
     *             error deactivating
     * @throws InterruptedException
     *             error waiting for deactivation
     * @see Listener#deactivate()
     */
    protected void deactivate() throws RSBException, InterruptedException {
        LOG.warning("invalid state exception during deactivate call");
        throw new IllegalStateException("subscriber already deactivated");
    }

    /**
     * Returns the listener being managed by this state instance.
     *
     * @return the listener instance
     */
    protected Listener getListener() {
        return this.listener;
    }

}
