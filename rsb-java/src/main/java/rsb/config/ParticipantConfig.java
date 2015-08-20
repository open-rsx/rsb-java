/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rsb.eventprocessing.EventReceivingStrategyFactory;
import rsb.eventprocessing.SingleThreadFactory;

/**
 * A class describing the configuration of Participant instances. Whenever
 * possible, this class contains API classes instead of configuration options.
 * Options, on the other hand, are maintained inside {@link rsb.util.Properties}
 * instances.
 *
 * @author swrede
 * @author jmoringe
 * @author jwienke
 */
public class ParticipantConfig {

    private final Map<String, TransportConfig> transportsByName =
            new HashMap<String, TransportConfig>();

    private EventReceivingStrategyFactory receivingStrategy =
            new SingleThreadFactory();

    private boolean introspectionEnabled = true;

    /**
     * Returns the internal map of available transport configurations by their
     * names.
     *
     * @return map from transport names to their configuration objects
     */
    public final Map<String, TransportConfig> getTransports() {
        return this.transportsByName;
    }

    /**
     * Indicates whether there is a configuration for a transport with the given
     * name.
     *
     * @param name
     *            name of the transport
     * @return <code>true</code> if a config for the transport exists, else
     *         <code>false</code>
     */
    public boolean hasTransport(final String name) {
        return this.transportsByName.containsKey(name);
    }

    /**
     * Returns a transport and, if necessary, creates the transport config.
     *
     * @param transportName
     *            name of the transport
     * @return config for that transport
     */
    public TransportConfig getOrCreateTransport(final String transportName) {
        if (this.transportsByName.containsKey(transportName)) {
            return this.transportsByName.get(transportName);
        } else {
            final TransportConfig newTransport =
                    new TransportConfig(transportName);
            this.transportsByName.put(transportName, newTransport);
            return newTransport;
        }
    }

    /**
     * Returns the set of transport configurations which represent enabled
     * transports.
     *
     * @return set of transport configurations
     */
    public Set<TransportConfig> getEnabledTransports() {
        final Set<TransportConfig> enabledTransports =
                new HashSet<TransportConfig>();
        for (final TransportConfig config : this.transportsByName.values()) {
            if (config.isEnabled()) {
                enabledTransports.add(config);
            }
        }
        return enabledTransports;
    }

    /**
     * Returns a factory for creating receiving strategy instances.
     *
     * @return factory
     */
    public EventReceivingStrategyFactory getReceivingStrategy() {
        return this.receivingStrategy;
    }

    /**
     * Setter method for the event receiving strategy factory to use.
     *
     * @param receivingStrategy
     *            factory to use for creating receiving strategy instances.
     */
    public void setReceivingStrategy(
            final EventReceivingStrategyFactory receivingStrategy) {
        this.receivingStrategy = receivingStrategy;
    }

    /**
     * Indicates whether introspection should be enabled for participants with
     * this config or not.
     *
     * @return <code>true</code> if participants shall be included in the
     *         introspection
     */
    public boolean isIntrospectionEnabled() {
        return this.introspectionEnabled;
    }

    /**
     * Sets whether introspection shall be enabled or not.
     *
     * @param introspectionEnabled
     *            <code>true</code> for enabling
     */
    public void setIntrospectionEnabled(final boolean introspectionEnabled) {
        this.introspectionEnabled = introspectionEnabled;
    }

    /**
     * Creates a deep copy of this instance. Most attributes are copied deeply.
     *
     * @return deep copy
     */
    public ParticipantConfig copy() {

        final ParticipantConfig copy = new ParticipantConfig();
        copy.receivingStrategy = this.receivingStrategy;
        // CHECKSTYLE.OFF: LineLength - no way to format this
        for (final Entry<String, TransportConfig> entry : this.transportsByName.entrySet()) {
            copy.transportsByName.put(entry.getKey(), entry.getValue().copy());
        }
        // CHECKSTYLE.ON: LineLength

        return copy;

    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder(35);
        builder.append(getClass().getName());
        builder.append("[transports=");
        builder.append(Arrays
                .toString(this.transportsByName.values().toArray()));
        builder.append(", receivingStrategy=");
        builder.append(this.receivingStrategy);
        builder.append(']');

        return builder.toString();

    }

    @Override
    public int hashCode() {
        // CHECKSTYLE.OFF: AvoidInlineConditionals - this method is more
        // readable with the inline conditionals
        final int prime = 31;
        int result = 17 * this.transportsByName.hashCode();
        result +=
                prime
                        * (this.receivingStrategy == null ? 0
                                : this.receivingStrategy.hashCode());
        result = prime * result + (this.introspectionEnabled ? 1231 : 1237);
        // CHECKSTYLE.ON: AvoidInlineConditionals
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ParticipantConfig)) {
            return false;
        }
        final ParticipantConfig other = (ParticipantConfig) obj;
        return this.transportsByName.equals(other.transportsByName)
                && this.receivingStrategy.equals(other.receivingStrategy)
                && this.introspectionEnabled == other.introspectionEnabled;
    }

}
