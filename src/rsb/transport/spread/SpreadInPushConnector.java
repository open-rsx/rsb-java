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
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConverterSelectionStrategy;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;

/**
 * An {@link InPushConnector} for the spread daemon network.
 *
 * @author jwienke
 */
public class SpreadInPushConnector implements InPushConnector {

    private static final Logger LOG = Logger
            .getLogger(SpreadInPushConnector.class.getName());

    private Scope scope;
    private final SpreadReceiver receiver;

    /**
     * Creates a new connector.
     *
     * @param spread
     *            the spread wrapper to use. Must not be active.
     * @param converters
     *            the converters to use for deserializing data
     */
    public SpreadInPushConnector(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        assert !spread.isActive() : "As the spread object is used for handling "
                + "our own activation state, it must not be active "
                + "when passed in.";
        this.receiver = new SpreadReceiver(spread, converters);
    }

    @Override
    public void activate() throws RSBException {
        LOG.finer("Activate called");
        assert this.scope != null;
        this.receiver.activate();
        try {
            this.receiver.join(SpreadUtilities.spreadGroupName(this.scope));
        } catch (final InterruptedException e) {
            // restore interruption state
            // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
            Thread.currentThread().interrupt();
            throw new InitializeException(e);
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        LOG.finer("Dectivate called");
        this.receiver.deactivate();
    }

    @Override
    public void setScope(final Scope scope) {
        LOG.log(Level.FINER, "Setting scope to {0}", new Object[] { scope });
        assert !this.receiver.isActive();
        this.scope = scope;
    }

    @Override
    public void addHandler(final EventHandler handler) {
        LOG.log(Level.FINER, "Adding handler {0}", new Object[] { handler });
        this.receiver.addHandler(handler);
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        LOG.log(Level.FINER, "Removing handler {0}", new Object[] { handler });
        final boolean removed = this.receiver.removeHandler(handler);
        LOG.log(Level.FINER, "Remove successfull for handler {0}: {1}",
                new Object[] { handler, removed });
        return removed;
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // we don't have to do anything for qos. all operations are safe
    }

    @Override
    public boolean isActive() {
        return this.receiver.isActive();
    }

    @Override
    public void notify(final Filter filter, final FilterAction action) {
        // transport level filtering is currently not supported
    }

}
