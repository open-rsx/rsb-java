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

import rsb.Activatable;
import spread.SpreadException;
import spread.SpreadMessage;

/**
 * Interface for classes that encapsulate and manages a connection to the spread
 * daemon. Such implementations maintain the membership information for this
 * connection and evaluate and enqueue all sorts of spread messages.
 *
 * @author jwienke
 */
public interface SpreadWrapper extends Activatable {

    /**
     * Describes the current state of the underlying connection.
     *
     * @author jwienke
     */
    enum State {
        ACTIVATED, DEACTIVATED
    };

    /**
     * Indicates the current state of the underlying connection.
     *
     * @return the status
     */
    State getStatus();

    /**
     * @return the spreadhost
     */
    InetAddress getSpreadhost();

    /**
     * @return the useTcpNoDelay
     */
    boolean isUseTcpNoDelay();

    /**
     * If connected, joins the specified group. Otherwise, call is ignored.
     * Calling this method does not wait until messages can effectively be
     * received. You have to wait for a membership message containing your
     * private group to be sure that this is the case.
     *
     * @param group
     *            group to join
     * @throws SpreadException
     *             error joining
     */
    void join(String group) throws SpreadException;

    /**
     * Sends the given message.
     *
     * @param msg
     *            the message to send
     * @throws SendException
     *             error sending the message
     */
    void send(DataMessage msg);

    /**
     * If connected and joined, leaves the specified group.
     *
     * @param group
     *            the group to leave
     */
    void leave(String group);

    /**
     * Returns the name of the private spread group for the wrapped connection.
     *
     * @return name of the group or <code>null</code> if not connected so far.
     */
    String getPrivateGroup();

    /**
     * Indicate whether this wrapper is currently successfully connected to a
     * spread daemon.
     *
     * @return <code>true</code> if connected, else <code>false</code>
     */
    boolean isConnected();

    /**
     * Receive the next message from the spread connection in groups this
     * wrapper has joined. Blocks if no message is available.
     *
     * @return the next message
     * @throws InterruptedIOException
     *             interrupted while waiting for the message
     * @throws SpreadException
     *             error while reading the next message
     */
    SpreadMessage receive() throws InterruptedIOException, SpreadException;

    /**
     * Indicate whether a shutdown was requested.
     *
     * @return <code>true</code> if requested.
     */
    boolean isShutdown();

}
