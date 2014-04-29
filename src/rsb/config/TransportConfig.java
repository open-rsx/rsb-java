/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2012 CoR-Lab, Bielefeld University
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

import rsb.converter.ConverterRepository;
import rsb.util.Properties;

/**
 * Represents the configuration of a single transport.
 *
 * @author swrede
 * @author jmoringe
 */
public class TransportConfig {

    private final String name;
    private boolean enabled;
    private Properties options;
    // TODO how to handle generics here correctly?
    private ConverterRepository<?> converters;

    /**
     * Creates a new instance.
     *
     * @param name
     *            name of the transport
     * @param enabled
     *            should this transport be enabled in participants or not?
     * @param options
     *            transport-specific options
     */
    public TransportConfig(final String name, final boolean enabled,
            final Properties options) {
        this.name = name;
        this.enabled = enabled;
        this.options = options;
    }

    /**
     * Creates a new instance.
     *
     * @param name
     *            name of the transport
     * @param enabled
     *            should this transport be enabled in participants or not?
     * @param options
     *            transport-specific options
     * @param converters
     *            the converter selection strategy to use for the transport.
     *            <code>null</code> indicates to use the system-wide
     *            configuration.
     */
    public TransportConfig(final String name, final boolean enabled,
            final Properties options, final ConverterRepository<?> converters) {
        this.name = name;
        this.enabled = enabled;
        this.options = options;
        this.converters = converters;
    }

    /**
     * Creates a new instance. The transport will be disabled and options will
     * be empty
     *
     * @param name
     *            name of the transport
     */
    public TransportConfig(final String name) {
        this(name, false, new Properties());
    }

    /**
     * Returns the name of the transport.
     *
     * @return name string
     */
    public String getName() {
        return this.name;
    }

    /**
     * Indicates whether the transport shall be enabled in participants or not.
     *
     * @return <code>true</code> if transport shall be enabled, else
     *         <code>false</code>
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets whether the transport shall be enabled in participants.
     *
     * @param enabled
     *            <code>true</code> to enable the transport, else
     *            <code>false</code>
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the transport-specific options.
     *
     * @return options specifically for the transport with a common prefix
     */
    public Properties getOptions() {
        return this.options;
    }

    /**
     * Sets the transport-specific options.
     *
     * @param options
     *            options specifically for this transport
     */
    public void setOptions(final Properties options) {
        this.options = options;
    }

    // TODO handle disambiguation options for converters

    /**
     * Returns the desired converter repository for the transport.
     *
     * @return converter repository to use or <code>null</code> in case the
     *         system-wide default shall be used.
     */
    public ConverterRepository<?> getConverters() {
        return this.converters;
    }

    /**
     * Returns the desired converter repository for the transport or the
     * provided default in case no desired instance is is specified.
     *
     * @param defaultInst
     *            instance to return in case no converter repository was
     *            specified inside this config
     * @return converter repository to use or <code>null</code> in case event
     *         the given default instance was <code>null</code>.
     */
    public ConverterRepository<?> getConverters(
            final ConverterRepository<?> defaultInst) {
        if (this.converters == null) {
            return defaultInst;
        } else {
            return this.converters;
        }
    }

    /**
     * Sets the converter repository to be used for this transport.
     *
     * @param converters
     *            converters to use or <code>null</code> to indicate use of
     *            system-wide converters
     */
    public void setConverters(final ConverterRepository<?> converters) {
        this.converters = converters;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder(42);
        builder.append(getClass().getName());
        builder.append("[name='");
        builder.append(this.name);
        builder.append("', enabled=");
        builder.append(this.enabled);
        builder.append(", options=");
        builder.append(this.options);
        builder.append(", converters=");
        builder.append(this.converters);
        builder.append(']');

        return builder.toString();

    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public int hashCode() {
        // CHECKSTYLE.OFF: AvoidInlineConditionals - this method is more
        // readable with the inline conditionals
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((this.converters == null) ? 0 : this.converters
                                .hashCode());
        result = prime * result + (this.enabled ? 1231 : 1237);
        result =
                prime * result
                        + ((this.name == null) ? 0 : this.name.hashCode());
        result =
                prime
                        * result
                        + ((this.options == null) ? 0 : this.options.hashCode());
        return result;
        // CHECKSTYLE.ON: AvoidInlineConditionals
    }

    @SuppressWarnings({ "PMD.NPathComplexity", "PMD.CyclomaticComplexity" })
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TransportConfig)) {
            return false;
        }
        final TransportConfig other = (TransportConfig) obj;
        if (this.converters == null) {
            if (other.converters != null) {
                return false;
            }
        } else if (!this.converters.equals(other.converters)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.options == null) {
            if (other.options != null) {
                return false;
            }
        } else if (!this.options.equals(other.options)) {
            return false;
        }
        return true;
    }

}
