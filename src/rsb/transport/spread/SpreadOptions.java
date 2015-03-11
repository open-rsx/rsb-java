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

/**
 * Encapsulates options required to make a connection to a spread daemon.
 *
 * @author jwienke
 */
public class SpreadOptions {

    private final String host;
    private final int port;
    private final boolean tcpNoDelay;

    /**
     * Creates a new instance for the specified options.
     *
     * @param host
     *            the host name or IP address of the spread daemon. Must not be
     *            <code>null</code> or empty.
     * @param port
     *            the port of the spread daemon, > 0
     * @param tcpNoDelay
     *            whether to connect with tcpnodelay or not
     * @throws IllegalArgumentException
     *             one of the given arguments does not meet the requirements
     */
    public SpreadOptions(final String host, final int port,
            final boolean tcpNoDelay) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException(
                    "Spread host must not be null or empty string. "
                            + "Instead a valid host name or ip address is required.");
        }
        if (port <= 0) {
            throw new IllegalArgumentException(
                    "Spread port must be a number > 0.");
        }

        this.host = host;
        this.port = port;
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * Returns the host of the spread daemon.
     *
     * @return string, not <code>null</code>, not empty
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Returns the port of the spread daemon.
     *
     * @return port > 0
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Indicates whether to use tcpnodelay for the connection to the daemon or
     * not.
     *
     * @return <code>true</code> for using tcpnodelay
     */
    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionals - auto-generated method
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime * result
                        + ((this.host == null) ? 0 : this.host.hashCode());
        result = prime * result + this.port;
        result = prime * result + (this.tcpNoDelay ? 1231 : 1237);
        return result;
    }

    // CHECKSTYLE.ON: AvoidInlineConditionals

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SpreadOptions)) {
            return false;
        }
        final SpreadOptions other = (SpreadOptions) obj;
        if (this.host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!this.host.equals(other.host)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (this.tcpNoDelay != other.tcpNoDelay) {
            return false;
        }
        return true;
    }

}
