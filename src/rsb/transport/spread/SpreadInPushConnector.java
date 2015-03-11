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
package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConverterSelectionStrategy;
import rsb.filter.AbstractFilterObserver;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;
import spread.SpreadException;

/**
 * An {@link InPushConnector} for the spread daemon network.
 *
 * @author jwienke
 */
public class SpreadInPushConnector extends AbstractFilterObserver implements
        InPushConnector, EventHandler {

    private static final Logger LOG = Logger
            .getLogger(SpreadInPushConnector.class.getName());

    private final Set<EventHandler> eventHandlers = new HashSet<EventHandler>();
    private ReceiverTask receiver;
    private final SpreadWrapper spread;
    private Scope scope;

    private final ConverterSelectionStrategy<ByteBuffer> inStrategy;

    /**
     * Creates a new connector.
     *
     * @param spread
     *            the spread wrapper to use. Must not be active.
     * @param inStrategy
     *            the converters to use for deserializing data
     */
    public SpreadInPushConnector(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> inStrategy) {
        assert !spread.isActive() : "As the spread object is used for handling "
                + "our own activation state, it must not be active "
                + "when passed in.";
        this.spread = spread;
        this.inStrategy = inStrategy;
    }

    @Override
    public void activate() throws RSBException {
        assert this.scope != null;

        this.receiver = new ReceiverTask(this.spread, this, this.inStrategy);
        // activate spread connection
        if (!this.spread.isActive()) {
            this.spread.activate();
        }
        this.receiver.setPriority(Thread.NORM_PRIORITY + 2);
        this.receiver.setName("ReceiverTask [grp="
                + this.spread.getPrivateGroup() + "]");
        this.receiver.start();

        try {
            joinSpreadGroup(this.scope);
        } catch (final SpreadException e) {
            throw new InitializeException(
                    "Unable to join spread group for scope '" + this.scope
                            + "' with hash '"
                            + SpreadUtilities.spreadGroupName(this.scope)
                            + "'.", e);
        }

    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (this.spread.isActive()) {
            LOG.fine("deactivating SpreadPort");
            this.spread.deactivate();
        }
        try {
            this.receiver.join();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setScope(final Scope scope) {
        assert !this.spread.isActive();
        this.scope = scope;
    }

    private void joinSpreadGroup(final Scope scope) throws SpreadException {
        assert this.spread.isActive();
        this.spread.join(SpreadUtilities.spreadGroupName(scope));

    }

    @Override
    public void addHandler(final EventHandler handler) {
        assert !isActive();
        assert handler != null;
        synchronized (this.eventHandlers) {
            this.eventHandlers.add(handler);
        }
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        assert !isActive();
        synchronized (this.eventHandlers) {
            return this.eventHandlers.remove(handler);
        }
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // we don't have to do anything for qos. all operations are safe
    }

    @Override
    public boolean isActive() {
        return this.spread.isActive();
    }

    @Override
    public void handle(final Event event) {
        synchronized (this.eventHandlers) {
            for (final EventHandler handler : this.eventHandlers) {
                handler.handle(event);
            }
        }
    }

}
