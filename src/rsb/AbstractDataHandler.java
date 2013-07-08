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
 * A handler that receives the user payload of an event by extracting the data
 * and casting them to the specified type.
 * 
 * @author swrede
 * @param <V>
 *            the desired target data type of the user handler. The event
 *            payload will be casted to this type
 */
public abstract class AbstractDataHandler<V> implements Handler {

    private static final Logger LOG = Logger
            .getLogger(AbstractDataHandler.class.getName());

    @SuppressWarnings({ "unchecked", "PMD.AvoidCatchingGenericException" })
    @Override
    public void internalNotify(final Event event) {
        try {
            this.handleEvent((V) event.getData());
        } catch (final RuntimeException ex) {
            LOG.warning("RuntimeException during event dispatching: "
                    + ex.getMessage() + " Re-throwing it.");
            throw ex;
        }
    }

    public abstract void handleEvent(V data);

}
