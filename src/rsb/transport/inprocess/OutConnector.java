/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.transport.inprocess;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;

/**
 * {@link rsb.transport.OutConnector} implementation for the inprocess
 * transport.
 *
 * @author jwienke
 */
public class OutConnector implements rsb.transport.OutConnector {

    private final Bus bus;
    private boolean active = false;
    private Scope scope = null;

    /**
     * Creates a new out connector operating on a given bus instance.
     *
     * @param bus
     *            the bus to use for sending events.
     */
    public OutConnector(final Bus bus) {
        this.bus = bus;
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // nothing to do here
    }

    @Override
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            if (this.active) {
                throw new IllegalStateException("Already active");
            }
            if (this.scope == null) {
                throw new IllegalStateException(
                        "Scope needs to be set before activating a connector.");
            }
            this.active = true;
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            if (!this.active) {
                throw new IllegalStateException("Not active");
            }
            this.active = false;
        }
    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.active;
        }
    }

    @Override
    public void push(final Event event) throws RSBException {
        event.getMetaData().setSendTime(0);
        this.bus.push(event);
    }

}
