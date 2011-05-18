/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.transport.spread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Random;
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
 * 
 */
public class SpreadWrapper implements RSBObject {

	// TODO think about sub-classing SpreadConnection
	// TODO leave the complex stuff for SpreadPort

	final static Logger log = Logger.getLogger(SpreadWrapper.class.getName());

	/**
	 * @return the status
	 */
	public State getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(State status) {
		this.status = status;
	}

	/**
	 * @return the spreadhost
	 */
	public InetAddress getSpreadhost() {
		return spreadhost;
	}

	/**
	 * @param spreadhost
	 *            the spreadhost to set
	 * @throws RSBException
	 */
	public void setSpreadhost(String spreadHostname) throws RSBException {
		try {
			spreadhost = InetAddress.getByName(spreadHostname);
		} catch (UnknownHostException e) {
			throw new RSBException(e.getMessage(), e);
		}
	}

	/**
	 * @return the useTcpNoDelay
	 */
	public boolean isUseTcpNoDelay() {
		return useTcpNoDelay;
	}

	/**
	 * @param useTcpNoDelay
	 *            the useTcpNoDelay to set
	 */
	public void setUseTcpNoDelay(boolean useTcpNoDelay) {
		this.useTcpNoDelay = useTcpNoDelay;
	}

	/**
	 * @param connectionLost
	 *            the connectionLost to set
	 */
	public void setConnectionLost(boolean connectionLost) {
		this.connectionLost = connectionLost;
	}

	enum State {
		ACTIVATED, DEACTIVATED
	};

	private State status = State.DEACTIVATED;

	// TODO better name needed for this variable, check Spread doc
	String name;
	String privGrpId;
	SpreadConnection conn;
	private Deque<SpreadGroup> groups = new ArrayDeque<SpreadGroup>();
	Properties props = Properties.getInstance();
	private int port;
	private InetAddress spreadhost = null;
	private boolean useTcpNoDelay = true;

	/** random number generator for connection names */
	private Random r = new Random();
	boolean shutdown = false;

	private boolean connectionLost = false;

	/*
	 * Create a new Manager, assuming a spread daemon on localhost, port 4803.
	 * 
	 * @param name Name for this manager
	 */
	public SpreadWrapper() {
		port = props.getPropertyAsInt("Spread.Port");
		try {
			// TODO refactor this to use a doman object SpreadHost (also for
			// connection checks and so on...
			// TODO handle this in a way that in this constructor no exceptions
			// may occur
			// TODO e.g., if we can't resolve the inetaddress, this coudl
			// already fail in the properties parsing
			setSpreadhost(props.getProperty("Spread.Host"));
		} catch (RSBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		useTcpNoDelay = props.getPropertyAsBool("Spread.TcpNoDelay");
	}

	/**
	 * Create a new Manager using the specified connection data for the Spread
	 * network.
	 * 
	 * @param name
	 *            Name for this manager
	 * @param spreadhost
	 *            hostname of the machine the spread daemon is running on
	 * @param port
	 *            for the spread daemon
	 */
	public SpreadWrapper(String spreadhost, int port)
			throws InitializeException, UnknownHostException {
		this.spreadhost = spreadhost != null ? InetAddress
				.getByName(spreadhost) : null;
		this.port = port;
		this.useTcpNoDelay = props.getPropertyAsBool("Spread.TcpNoDelay");
		makeConnection("sp-", true, false);
	}

	// TODO think about prefixes and factory methods
	public SpreadWrapper(String spreadhost, int port, boolean sendOnly)
			throws InitializeException, UnknownHostException {
		this.spreadhost = spreadhost != null ? InetAddress
				.getByName(spreadhost) : null;
		this.port = port;
		this.useTcpNoDelay = props.getPropertyAsBool("Spread.TcpNoDelay");
		makeConnection("sp-", false, true);
	}

	public void join(String group) throws SpreadException {
		checkConnection();
		SpreadGroup grp = new SpreadGroup();
		try {
			grp.join(conn, group);
			groups.add(grp);
			log.info("Joined SpreadGroup with name: " + group);
		} catch (SpreadException e) {
			// log.info("Could not join group!");
			throw e;
		}
	}

	protected boolean isConnectionLost() {
		synchronized (conn) {
			return connectionLost;
		}
	}

	protected void checkConnection() {
		if (conn == null)
			return; // not initialized yet
		if (isConnectionLost()) {
			log.severe("lost connection to spread daemon");
			throw new ConnectionLostException(
					"Lost connection to spread daemon");
		}
		if (!conn.isConnected() && !shutdown) {
			log.severe("lost connection to spread daemon");
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
	 * @throws CommunicationException
	 *             when no connection could be established
	 */
	void makeConnection(String prefix, boolean mship, boolean sendOnly)
			throws InitializeException {
		SpreadException ex = null;
		String hostmsg = "";
		for (int i = 0; i < 50; i++) {
			name = prefix + r.nextInt(999999);
			try {
				// if spreadhost is null, a connection to localhost is tried
				conn = new SpreadConnection();
				if (spreadhost == null) {
					hostmsg = "localhost";
				} else {
					hostmsg = spreadhost.getHostName();
				}
				conn.connect(spreadhost, port, name, false, mship);
				conn.setTcpNoDelay(this.useTcpNoDelay);
				log.fine("Connected to " + spreadhost + ":" + port
						+ ". Name = " + name);
				privGrpId = conn.getPrivateGroup().toString();
				// instantiate our own listener thread
				log.fine("Spread connection's private group id is: "
						+ privGrpId);
				return;
			} catch (SpreadException e) {
				ex = e;
			}
			log.info("reoccuring SpreadException during connect to daemon: "
					+ ex.getMessage());
		}
		// if we get here, all connection attempts failed
		throw new InitializeException("Could not create spread connection "
				+ "host=" + hostmsg + ", port=" + port, ex);
	}

	public boolean send(DataMessage msg) {
		// TODO check whether we should rethrow the exceptions
		if (conn != null) {
			checkConnection();
			try {
				conn.multicast(msg.getSpreadMessage());
				return true;
			} catch (SpreadException e) {
				log.warning("SpreadException occurred during multicast send of message, reason: "
						+ e.getMessage());
				return false;
			} catch (SerializeException e) {
				log.warning("SerializeException occurred during multicast send of message, reason: "
						+ e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}

	public void deactivate() throws RSBException {
		// protect from listener thread when connection is lost
		synchronized (conn) {
			shutdown = true;
			// try to leave all groups joined before
			Iterator<SpreadGroup> it = groups.iterator();
			while (it.hasNext()) {
				SpreadGroup grp = it.next();
				try {
					grp.leave();
					log.info("SpreadGroup '" + grp + "' has been left.");
				} catch (SpreadException e) {
					// ignored
					log.info("Caught a SpreadException while leaving group '"
							+ grp + "': " + e.getMessage());
				}
				it.remove();
			}
			// close connection
			try {
				conn.disconnect();
			} catch (SpreadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			status = State.DEACTIVATED;

		}
	}

	public void leave(String type) {
		if (status == State.ACTIVATED) {
			Iterator<SpreadGroup> it = groups.iterator();
			while (it.hasNext()) {
				SpreadGroup grp = it.next();
				if (grp.toString().equals(type)) {
					try {
						grp.leave();
					} catch (SpreadException e) {
						// this should not happen
						e.printStackTrace();
					}
					it.remove();
					log.info("SpreadGroup '" + grp + "' has been left.");
					break;
				}
				if (!it.hasNext()) {
					log.warning("Couldn't leave requested group with id: "
							+ type);
				}
			}
		}
	}

	public synchronized void activate() throws InitializeException {
		makeConnection("sp-", true, false);
		status = State.ACTIVATED;
	}

	public boolean isActivated() {
		if (status == State.ACTIVATED) {
			return true;
		}
		return false;
	}

	/**
	 * Returns name used for connection to spread daemon
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public String getPrivateGroup() {
		return privGrpId;
	}

}
