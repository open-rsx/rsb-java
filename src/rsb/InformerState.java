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
 * Interface specification for state classes used to represent the state pattern
 * in {@link Informer} instances.
 *
 * @author swrede
 *
 * @param <DataType>
 *            the type of data to be sent by the informer
 */
public class InformerState<DataType> {

    private static final String NOT_ACTIVE_MESSAGE = "informer not activated";

    private static final Logger LOG = Logger.getLogger(InformerState.class
            .getName());

    /**
     * The informer being managed by this state class.
     */
    private final Informer<DataType> informer;

    /**
     * Constructor.
     *
     * @param informer
     *            the informer being managed by this state class.
     */
    protected InformerState(final Informer<DataType> informer) {
        this.informer = informer;
    }

    /**
     * Implements {@link Informer#activate()}.
     *
     * @throws RSBException
     *             rsb error activating the informer
     */
    protected void activate() throws RSBException {
        LOG.warning("invalid state exception during activate call");
        throw new IllegalStateException(NOT_ACTIVE_MESSAGE);
    }

    /**
     * Implements {@link Informer#deactivate()}.
     *
     * @throws RSBException
     *             generic error
     * @throws InterruptedException
     *             error while waiting for termination
     */
    protected void deactivate() throws RSBException, InterruptedException {
        LOG.warning("invalid state exception during deactivate call");
        throw new IllegalStateException(NOT_ACTIVE_MESSAGE);
    }

    /**
     * Implements {@link Informer#send(Event)}.
     *
     * @param event
     *            event to send
     * @return sent event with changed meta data
     * @throws RSBException
     *             sending error
     */
    protected Event send(@SuppressWarnings("unused") final Event event)
            throws RSBException {
        LOG.warning("Method send(Event) not implemented for informer state "
                + this);
        throw new IllegalStateException(NOT_ACTIVE_MESSAGE);
    }

    /**
     * Implements {@link Informer#send(Object)}.
     *
     * @param data
     *            data to send
     * @return sent event with set meta data
     * @throws RSBException
     *             sending error
     */
    protected Event send(@SuppressWarnings("unused") final DataType data)
            throws RSBException {
        LOG.warning("Method send(DataType) not implemented for informer state "
                + this);
        throw new IllegalStateException(NOT_ACTIVE_MESSAGE);
    }

    /**
     * Returns the {@link Informer} instance this state operates in.
     *
     * @return informer
     */
    public Informer<DataType> getInformer() {
        return this.informer;
    }

}
