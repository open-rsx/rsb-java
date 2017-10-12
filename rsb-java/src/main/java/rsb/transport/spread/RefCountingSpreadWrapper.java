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
package rsb.transport.spread;

import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.AbstractActivatable;
import rsb.RSBException;
import spread.SpreadException;
import spread.SpreadMessage;

/**
 * A facade for {@link SpreadWrapper} instances which counts calls to
 * {@link #activate()} and {@link #deactivate()} and only passes these calls
 * down to a wrapped instance of {@link SpreadWrapper} in case they are the
 * first or the last.
 *
 * @author jwienke
 */
public class RefCountingSpreadWrapper extends AbstractActivatable
                                      implements SpreadWrapper {

    private static final Logger LOG = Logger
            .getLogger(RefCountingSpreadWrapper.class.getName());

    private final SpreadWrapper wrapped;
    private int counter = 0;

    /**
     * Creates a new instance wrapping an the given instance.
     *
     * @param wrapped
     *            instance to wrap.
     */
    public RefCountingSpreadWrapper(final SpreadWrapper wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            LOG.log(Level.FINE,
                    "Receveived activate call with counter being {0} before this call",
                    new Object[] { this.counter });
            if (this.counter == 0) {
                LOG.fine("Activiating underlying SpreadWrapper instance");
                this.wrapped.activate();
            }
            ++this.counter;
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            LOG.log(Level.FINE,
                    "Receveived deactivate call with counter being {0} before this call",
                    new Object[] { this.counter });
            // at least somehow try to shield against activation protocol
            // violations
            if (this.counter <= 0) {
                throw new IllegalStateException("already deactivated");
            }
            --this.counter;
            if (this.counter == 0) {
                LOG.fine("Deactiviating underlying SpreadWrapper instance");
                this.wrapped.deactivate();
            }
        }
    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.wrapped.isActive();
        }
    }

    @Override
    public ConnectionState getStatus() {
        return this.wrapped.getStatus();
    }

    @Override
    public InetAddress getSpreadhost() {
        return this.wrapped.getSpreadhost();
    }

    @Override
    public boolean isUseTcpNoDelay() {
        return this.wrapped.isUseTcpNoDelay();
    }

    @Override
    public void join(final String group) throws SpreadException {
        this.wrapped.join(group);
    }

    @Override
    public void send(final DataMessage msg) {
        this.wrapped.send(msg);
    }

    @Override
    public void leave(final String group) {
        this.wrapped.leave(group);
    }

    @Override
    public String getPrivateGroup() {
        return this.wrapped.getPrivateGroup();
    }

    @Override
    public boolean isConnected() {
        return this.wrapped.isConnected();
    }

    @Override
    public SpreadMessage receive() throws InterruptedIOException,
            SpreadException {
        return this.wrapped.receive();
    }

    @Override
    public boolean isShutdown() {
        return this.wrapped.isShutdown();
    }

    @Override
    public URI getTransportUri() {
        return this.wrapped.getTransportUri();
    }

}
