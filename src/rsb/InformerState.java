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

public class InformerState<T> {

    private static final String NOT_ACTIVE_MESSAGE = "informer not activated";

    private final static Logger LOG = Logger.getLogger(InformerState.class
            .getName());

    protected Informer<T> ctx;

    protected InformerState() {
        // prevent instantiation without subclassing
    }

    protected InformerState(final Informer<T> ctx) {
        this.ctx = ctx;
    }

    protected void activate() throws InitializeException {
        LOG.warning("invalid state exception during activate call");
        throw new InvalidStateException(NOT_ACTIVE_MESSAGE);
    }

    protected void deactivate() throws RSBException, InterruptedException {
        LOG.warning("invalid state exception during deactivate call");
        throw new InvalidStateException(NOT_ACTIVE_MESSAGE);
    }

    protected Event send(@SuppressWarnings("unused") final Event event)
            throws RSBException {
        LOG.warning("invalid state exception during call to send");
        throw new InvalidStateException(NOT_ACTIVE_MESSAGE);
    }

    protected Event send(@SuppressWarnings("unused") final T data)
            throws RSBException {
        LOG.warning("invalid state exception during call to send");
        throw new InvalidStateException(NOT_ACTIVE_MESSAGE);
    }

}
