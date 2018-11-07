/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb;

/**
 * {@link ParticipantCreateArgs} for {@link rsb.patterns.RemoteServer}
 * instances.
 *
 * @author jwienke
 * @author jmoringe
 */
// We want method chaining here, silence linguist
@SuppressWarnings("PMD.LinguisticNaming")
public class RemoteServerCreateArgs extends
        ParticipantCreateArgs<RemoteServerCreateArgs> {

    /**
     * The timeout used when waiting for RPC replies. Initialized with 0 which
     * indicates to wait forever.
     */
    private Double timeout = null;

    /**
     * Sets the timeout used when waiting for method replies.
     *
     * @param timeout
     *            a timeout in seconds or 0 to wait forever. <code>null</code>
     *            means that the implementation shall choose a timeout.
     * @return this instance
     */
    public RemoteServerCreateArgs setTimeout(final Double timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Returns the timeout to wait for method replies.
     *
     * @return timeout in seconds, 0 indicating to wait forever,
     *         <code>null</code> indicating that implementations may decide on a
     *         reasonable timeout.
     */
    public Double getTimeout() {
        return this.timeout;
    }

}
