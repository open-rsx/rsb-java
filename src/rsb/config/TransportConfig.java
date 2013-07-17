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

    // TODO converter

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
     * Indicates whether the transport shall be enabled in participants or not
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

    // TODO handle disambiguation options
    // TODO use this
    // public List<ConverterSignature> getConverters() {
    // final ArrayList<ConverterSignature> result = new
    // ArrayList<ConverterSignature>();
    // // TODO add string
    // // this is for Spread transport only currently
    // result.add(new ConverterSignature("utf-8-string", String.class));
    // return result;
    // }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder(30);
        builder.append(getClass().getName());
        builder.append("[name='");
        builder.append(this.name);
        builder.append("', enabled=");
        builder.append(this.enabled);
        builder.append(", options=");
        builder.append(this.options);
        builder.append(']');

        return builder.toString();

    }

}
