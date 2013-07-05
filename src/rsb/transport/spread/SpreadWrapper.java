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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.RSBException;
import rsb.RSBObject;
import rsb.util.Properties;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;

/**
 * This class encapsulates and manages a connection to the spread daemon.
 * Thereby, it maintains the membership information for this connection and
 * evaluates and enqueues all sorts of spread messages.
 * 
 * @author swrede
 */
public class SpreadWrapper implements RSBObject {

    // TODO think about sub-classing SpreadConnection
    // TODO leave the complex stuff for SpreadPort

    final static Logger LOG = Logger.getLogger(SpreadWrapper.class.getName());

    /**
     * @return the status
     */
    public State getStatus() {
        return this.status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(final State status) {
        this.status = status;
    }

    /**
     * @return the spreadhost
     */
    public InetAddress getSpreadhost() {
        return this.spreadhost;
    }

    /**
     * @param spreadHostname
     *            the spreadhost to set
     * @throws RSBException
     */
    private void setSpreadhost(final String spreadHostname) throws RSBException {
        try {
            this.spreadhost = InetAddress.getByName(spreadHostname);
        } catch (final UnknownHostException e) {
            throw new RSBException(e.getMessage(), e);
        }
    }

    /**
     * @return the useTcpNoDelay
     */
    public boolean isUseTcpNoDelay() {
        return this.useTcpNoDelay;
    }

    /**
     * @param useTcpNoDelay
     *            the useTcpNoDelay to set
     */
    public void setUseTcpNoDelay(final boolean useTcpNoDelay) {
        this.useTcpNoDelay = useTcpNoDelay;
    }

    /**
     * @param connectionLost
     *            the connectionLost to set
     */
    public void setConnectionLost(final boolean connectionLost) {
        this.connectionLost = connectionLost;
    }

    enum State {
        ACTIVATED, DEACTIVATED
    };

    private State status = State.DEACTIVATED;

    String privGrpId;
    SpreadConnection conn;
    private final Deque<SpreadGroup> groups = new ArrayDeque<SpreadGroup>();
    Properties props = Properties.getInstance();
    private final int port;
    private InetAddress spreadhost = null;
    private boolean useTcpNoDelay = true;

    /** random number generator for connection names */
    boolean shutdown = false;

    private boolean connectionLost = false;

    /**
     * Create a new Manager, assuming a spread daemon on localhost, port 4803.
     */
    public SpreadWrapper() {
        this.port = this.props.getPropertyAsInt("transport.spread.port");
        try {
            // TODO refactor this to use a doman object SpreadHost (also for
            // connection checks and so on...
            // TODO handle this in a way that in this constructor no exceptions
            // may occur
            // TODO e.g., if we can't resolve the inetaddress, this coudl
            // already fail in the properties parsing
            this.setSpreadhost(this.props.getProperty("transport.spread.host"));
        } catch (final RSBException e) {
        }
        this.useTcpNoDelay = this.props
                .getPropertyAsBool("transport.spread.tcpnodelay");
    }

    /**
     * Create a new Manager using the specified connection data for the Spread
     * network.
     * 
     * @param spreadhost
     *            hostname of the machine the spread daemon is running on
     * @param port
     *            for the spread daemon
     */
    public SpreadWrapper(final String spreadhost, final int port)
            throws InitializeException, UnknownHostException {
        this.spreadhost = spreadhost != null ? InetAddress
                .getByName(spreadhost) : null;
        this.port = port;
        this.useTcpNoDelay = this.props
                .getPropertyAsBool("transport.spread.tcpnodelay");
    }

    // TODO think about prefixes and factory methods
    public SpreadWrapper(final String spreadhost, final int port,
            final boolean sendOnly) throws InitializeException,
            UnknownHostException {
        this.spreadhost = spreadhost != null ? InetAddress
                .getByName(spreadhost) : null;
        this.port = port;
        this.useTcpNoDelay = this.props
                .getPropertyAsBool("transport.spread.tcpnodelay");
    }

    public void join(final String group) throws SpreadException {
        this.checkConnection();
        final SpreadGroup grp = new SpreadGroup();
        try {
            grp.join(this.conn, group);
            this.groups.add(grp);
            LOG.fine("Joined SpreadGroup with name: " + group);
        } catch (final SpreadException e) {
            // log.info("Could not join group!");
            throw e;
        }
    }

    protected boolean isConnectionLost() {
        synchronized (this.conn) {
            return this.connectionLost;
        }
    }

    protected void checkConnection() {
        if (this.conn == null) {
            return; // not initialized yet
        }
        if (this.isConnectionLost()) {
            LOG.severe("lost connection to spread daemon");
            throw new ConnectionLostException(
                    "Lost connection to spread daemon");
        }
        if (!this.conn.isConnected() && !this.shutdown) {
            LOG.severe("lost connection to spread daemon");
            throw new ConnectionLostException(
                    "Lost connection to spread daemon");
        }
    }

    /**
     * Create a new SpreadConnection. Generates a new name randomly, using the
     * specified prefix. To allow for name collisions, try a couple of times
     * before giving up.
     * 
     * @param prefix
     *            prefix to use in name, should be short
     * @param mship
     *            True - receive membership messages; false - don't
     */
    void makeConnection(final String prefix, final boolean mship,
            final boolean sendOnly) throws InitializeException {
        SpreadException ex = null;
        String hostmsg = "";
        for (int i = 0; i < this.props
                .getPropertyAsInt("transport.spread.retry"); i++) {
            try {
                // if spreadhost is null, a connection to localhost is tried
                this.conn = new SpreadConnection();
                if (this.spreadhost == null) {
                    hostmsg = "localhost";
                } else {
                    hostmsg = this.spreadhost.getHostName();
                }
                this.conn.connect(this.spreadhost, this.port, null, false,
                        mship);
                this.conn.setTcpNoDelay(this.useTcpNoDelay);
                LOG.fine("Connected to " + this.spreadhost + ":" + this.port
                        + ". Name = " + this.conn.getPrivateGroup().toString());
                this.privGrpId = this.conn.getPrivateGroup().toString();
                // instantiate our own listener thread
                LOG.fine("Spread connection's private group id is: "
                        + this.privGrpId);
                return;
            } catch (final SpreadException e) {
                ex = e;
            }
            LOG.info("reoccuring SpreadException during connect to daemon: "
                    + ex.getMessage());
        }
        // if we get here, all connection attempts failed
        throw new InitializeException("Could not create spread connection "
                + "host=" + hostmsg + ", port=" + this.port, ex);
    }

    public boolean send(final DataMessage msg) {

        // check group names for length
        for (final String group : msg.getGroups()) {
            if (group.length() > 31) {
                throw new IllegalArgumentException(
                        "Group with name '"
                                + group
                                + "' is too long for spread, only 31 characters are allowed.");
            }
        }

        // TODO check whether we should rethrow the exceptions
        if (this.conn != null) {
            this.checkConnection();
            try {
                this.conn.multicast(msg.getSpreadMessage());
                return true;
            } catch (final SpreadException e) {
                LOG.warning("SpreadException occurred during multicast send of message, reason: "
                        + e.getMessage());
                return false;
            } catch (final SerializeException e) {
                LOG.warning("SerializeException occurred during multicast send of message, reason: "
                        + e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void deactivate() throws RSBException {
        // protect from listener thread when connection is lost
        synchronized (this.conn) {
            this.shutdown = true;
            // try to leave all groups joined before
            final Iterator<SpreadGroup> it = this.groups.iterator();
            while (it.hasNext()) {
                final SpreadGroup grp = it.next();
                try {
                    grp.leave();
                    LOG.fine("SpreadGroup '" + grp + "' has been left.");
                } catch (final SpreadException e) {
                    // ignored
                    LOG.info("Caught a SpreadException while leaving group '"
                            + grp + "': " + e.getMessage());
                }
                it.remove();
            }
            // close connection
            try {
                this.conn.disconnect();
            } catch (final SpreadException e) {
            }
            this.status = State.DEACTIVATED;

        }
    }

    public void leave(final String type) {
        if (this.status == State.ACTIVATED) {
            final Iterator<SpreadGroup> it = this.groups.iterator();
            while (it.hasNext()) {
                final SpreadGroup grp = it.next();
                if (grp.toString().equals(type)) {
                    try {
                        grp.leave();
                    } catch (final SpreadException e) {
                        // this should not happen
                        assert (false);
                        e.printStackTrace();
                    }
                    it.remove();
                    LOG.info("SpreadGroup '" + grp + "' has been left.");
                    break;
                }
                if (!it.hasNext()) {
                    LOG.warning("Couldn't leave requested group with id: "
                            + type);
                }
            }
        }
    }

    @Override
    public synchronized void activate() throws InitializeException {
        this.makeConnection("sp-", true, false);
        this.status = State.ACTIVATED;
    }

    @Override
    public boolean isActive() {
        return this.status == State.ACTIVATED;
    }

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

}
