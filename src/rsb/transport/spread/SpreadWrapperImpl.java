/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.RSBException;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 * This class encapsulates and manages a connection to the spread daemon.
 * Thereby, it maintains the membership information for this connection and
 * evaluates and enqueues all sorts of spread messages.
 *
 * @author swrede
 */
public class SpreadWrapperImpl implements SpreadWrapper {

    private static final String CONNECTION_LOST_MSG =
            "Lost connection to spread daemon";

    private static final Logger LOG = Logger.getLogger(SpreadWrapperImpl.class
            .getName());

    private State status = State.DEACTIVATED;

    private String privGrpId;
    private SpreadConnection conn;
    private final Deque<SpreadGroup> groups = new ArrayDeque<SpreadGroup>();
    private final int port;
    private InetAddress spreadhost = null;
    private final boolean useTcpNoDelay;
    private boolean shutdown = false;

    /**
     * Creates a new instance for the given settings. Connection will be
     * established during {@link #activate()}.
     *
     * @param options
     *            the options to use for the connection
     * @throws UnknownHostException
     *             host name cannot be resolved
     */
    public SpreadWrapperImpl(final SpreadOptions options)
            throws UnknownHostException {
        this.port = options.getPort();
        this.setSpreadhost(options.getHost());
        this.useTcpNoDelay = options.isTcpNoDelay();
    }

    @Override
    public State getStatus() {
        return this.status;
    }

    @Override
    public InetAddress getSpreadhost() {
        return this.spreadhost;
    }

    /**
     * @param spreadHostname
     *            the spreadhost to set
     * @throws UnknownHostException
     *             the host cannot be resolved
     */
    private void setSpreadhost(final String spreadHostname)
            throws UnknownHostException {
        this.spreadhost = InetAddress.getByName(spreadHostname);
    }

    @Override
    public boolean isUseTcpNoDelay() {
        return this.useTcpNoDelay;
    }

    @Override
    public void join(final String group) throws SpreadException {
        this.checkConnection();
        final SpreadGroup grp = new SpreadGroup();
        grp.join(this.conn, group);
        this.groups.add(grp);
        LOG.fine("Joined SpreadGroup with name: " + group);
    }

    private void checkConnection() {
        if (this.conn == null) {
            return; // not initialized yet
        }
        if (!this.conn.isConnected() && !this.shutdown) {
            LOG.severe(CONNECTION_LOST_MSG);
            throw new ConnectionLostException(CONNECTION_LOST_MSG);
        }
    }

    /**
     * Create a new SpreadConnection. Generates a new name randomly.To allow for
     * name collisions, try a couple of times before giving up.
     *
     * @param mship
     *            True - receive membership messages; false - don't
     * @throws InitializeException
     *             error connecting
     */
    void makeConnection(final boolean mship) throws InitializeException {

        try {
            // if spreadhost is null, a connection to localhost is tried
            this.conn = new SpreadConnection();
            this.conn.connect(this.spreadhost, this.port, null, false, mship);
            this.conn.setTcpNoDelay(this.useTcpNoDelay);
            LOG.fine("Connected to " + this.spreadhost + ":" + this.port
                    + ". Name = " + this.conn.getPrivateGroup().toString());
            this.privGrpId = this.conn.getPrivateGroup().toString();
            // instantiate our own listener thread
            LOG.fine("Spread connection's private group id is: "
                    + this.privGrpId);
        } catch (final SpreadException e) {
            LOG.log(Level.WARNING,
                    "reoccuring SpreadException during connect to daemon", e);
            // if we get here, all connection attempts failed
            throw new InitializeException("Could not create spread connection "
                    + "host=" + this.spreadhost.getHostName() + ", port="
                    + this.port, e);
        }

    }

    @Override
    public boolean send(final DataMessage msg) {

        // check group names for length
        for (final String group : msg.getGroups()) {
            if (group.length() > SpreadUtilities.MAX_GROUP_NAME_LENGTH) {
                throw new IllegalArgumentException("Group with name '" + group
                        + "' is too long for spread, "
                        + "only 31 characters are allowed.");
            }
        }

        // TODO check whether we should rethrow the exceptions
        if (this.conn == null) {
            return false;
        }

        this.checkConnection();
        try {
            this.conn.multicast(msg.getSpreadMessage());
            return true;
        } catch (final SpreadException e) {
            LOG.log(Level.WARNING,
                    "SpreadException occurred during multicast send of message",
                    e);
            return false;
        } catch (final SerializeException e) {
            LOG.log(Level.WARNING,
                    "SerializeException occurred during multicast send of message",
                    e);
            return false;
        }

    }

    @Override
    public void deactivate() throws RSBException {
        // protect from listener thread when connection is lost
        synchronized (this.conn) {
            this.shutdown = true;
            // try to leave all groups joined before
            final Iterator<SpreadGroup> groupIt = this.groups.iterator();
            while (groupIt.hasNext()) {
                final SpreadGroup grp = groupIt.next();
                try {
                    grp.leave();
                    LOG.log(Level.FINE, "SpreadGroup {0} has been left.", grp);
                } catch (final SpreadException e) {
                    // ignored
                    LOG.log(Level.WARNING,
                            "Caught a SpreadException while leaving group '"
                            + grp + "': " + e.getMessage(), e);
                }
                groupIt.remove();
            }
            // close connection
            try {
                this.conn.disconnect();
            } catch (final SpreadException e) {
                LOG.log(Level.INFO, "Error disconnecting", e);
            }
            this.status = State.DEACTIVATED;

        }
    }

    @Override
    public void leave(final String group) {
        if (this.status == State.ACTIVATED) {
            final Iterator<SpreadGroup> groupIt = this.groups.iterator();
            while (groupIt.hasNext()) {
                final SpreadGroup grp = groupIt.next();
                if (grp.toString().equals(group)) {
                    try {
                        grp.leave();
                    } catch (final SpreadException e) {
                        // this should not happen
                        assert false;
                        LOG.log(Level.WARNING, "Error leaving spread group "
                                + group, e);
                    }
                    groupIt.remove();
                    LOG.info("SpreadGroup '" + grp + "' has been left.");
                    break;
                }
                if (!groupIt.hasNext()) {
                    LOG.warning("Couldn't leave requested group with id: "
                            + group);
                }
            }
        }
    }

    @Override
    public void activate() throws InitializeException {
        synchronized (this) {
            this.makeConnection(true);
            this.status = State.ACTIVATED;
        }
    }

    @Override
    public boolean isActive() {
        return this.status == State.ACTIVATED;
    }

    @Override
    public String getPrivateGroup() {
        return this.privGrpId;
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.status == State.ACTIVATED) {
            LOG.severe("Finalize called while status is activated.");
        }
        super.finalize();
    }

    @Override
    public boolean isConnected() {
        return this.conn.isConnected();
    }

    @Override
    public SpreadMessage receive() throws InterruptedIOException,
            SpreadException {
        return this.conn.receive();
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }

}
