/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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

import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.transport.AbstractConnector;

/**
 * Base class for inprocess connectors handling the activation logic.
 *
 * @author jwienke
 */
abstract class ConnectorBase extends AbstractConnector {

    private final Bus bus;
    private boolean active = false;
    private Scope scope = null;

    protected ConnectorBase(final Bus bus) {
        this.bus = bus;
    }

    protected Bus getBus() {
        return this.bus;
    }

    /**
     * Returns the scope of this connector.
     *
     * @return scope or <code>null</code> if not yet set
     */
    protected Scope getScope() {
        return this.scope;
    }

    // prevent repetition of default behavior
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // nothing to do here
    }

    @Override
    public void setScope(final Scope scope) {
        synchronized (this) {
            if (isActive()) {
                throw new IllegalStateException(
                        "Scope can only be set when not active.");
            }
            this.scope = scope;
        }
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            if (isActive()) {
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
            if (!isActive()) {
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

}
