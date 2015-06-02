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

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import rsb.InitializeException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.InPushConnector;
import rsb.util.Properties;

/**
 * An implementation which tries to reuse underlying {@link SpreadWrapper}
 * instances of the connectors in case options match. Ideally, if all options
 * match, only a single connection exists.
 *
 * @author jwienke
 */
public class SharedInPushConnectorFactory implements InPushConnectorFactory {

    /**
     * Options specifying all static properties of a
     * {@link SpreadInPushConnector} instance.
     *
     * @author jwienke
     */
    private static class ListeningOptions {

        private final SpreadOptions spreadOptions;
        private final ConverterSelectionStrategy<ByteBuffer> converters;

        public ListeningOptions(final SpreadOptions spreadOptions,
                final ConverterSelectionStrategy<ByteBuffer> converters) {
            this.spreadOptions = spreadOptions;
            this.converters = converters;
        }

        // CHECKSTYLE.OFF: InlineConditionals - generated method
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                    prime
                            * result
                            + ((this.converters == null) ? 0 : this.converters
                                    .hashCode());
            result =
                    prime
                            * result
                            + ((this.spreadOptions == null) ? 0
                                    : this.spreadOptions.hashCode());
            return result;
        }

        // CHECKSTYLE.ON: InlineConditionals

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ListeningOptions)) {
                return false;
            }
            final ListeningOptions other = (ListeningOptions) obj;
            if (this.converters == null) {
                if (other.converters != null) {
                    return false;
                }
            } else if (!this.converters.equals(other.converters)) {
                return false;
            }
            if (this.spreadOptions == null) {
                if (other.spreadOptions != null) {
                    return false;
                }
            } else if (!this.spreadOptions.equals(other.spreadOptions)) {
                return false;
            }
            return true;
        }

    }

    private final Map<ListeningOptions, SpreadMultiReceiver> spreadReceiversByOptions =
            new HashMap<ListeningOptions, SpreadMultiReceiver>();

    // TODO remove duplication
    private SpreadWrapper createSpreadWrapper(final SpreadOptions options)
            throws InitializeException {
        try {
            return new SpreadWrapperImpl(options);
        } catch (final NumberFormatException e) {
            throw new InitializeException(
                    "Unable to parse spread port from properties.", e);
        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve host name "
                    + options.getHost(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public InPushConnector create(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {
        final SpreadOptions spreadOptions =
                ConfigParseUtilities.spreadOptionsFromProperties(properties);
        final ListeningOptions options =
                new ListeningOptions(spreadOptions,
                        (ConverterSelectionStrategy<ByteBuffer>) converters);
        synchronized (this.spreadReceiversByOptions) {
            // TODO make this actually work
            // this always leaves the SpreadWrapper instances in the map since
            // we know that reactivation is possible for these instances
            if (!this.spreadReceiversByOptions.containsKey(options)) {
                // CHECKSTYLE.OFF: LineLength - hard to avoid with eclipse
                this.spreadReceiversByOptions
                        .put(options,
                                new SpreadMultiReceiver(
                                        new SpreadReceiver(
                                                createSpreadWrapper(spreadOptions),
                                                (ConverterSelectionStrategy<ByteBuffer>) converters)));
                // CHECKSTYLE.ON: LineLength
            }
            return new MultiSpreadInPushConnector(
                    this.spreadReceiversByOptions.get(options));
        }
    }

}
