/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
package rsb.transport.socket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import rsb.protocol.NotificationType.Notification;

/**
 * Instances of this class provide access to a socket-based bus.
 * It is transparent for clients (connectors) of this class whether
 * is accessed by running the bus server or by connecting to the bus
 * server as a client.
 * 
 * This class offers methods for sending and receiving events to this
 * bus as well as registration of internal Connectors (inward) and 
 * Connections (outward) which allow to send event notifications to
 * external participants. 
 * 
 * @author swrede
 *
 */
public class Bus {
	
	protected static Logger log = Logger.getLogger(Bus.class.getName());
	
	ConcurrentLinkedQueue<BusConnection> connections = new ConcurrentLinkedQueue<BusConnection>();
	
	public void handleIncoming() {
		// TODO handle incoming notifications and dispatch these to connectors
	}
	
	/**
	 * Distribute event notification to connected participants.
	 */
	public void handleOutgoing(Notification notification) {
		// TODO check if Bus needs to be locked
		// 1. Broadcast notification to connections
		for (BusConnection con : connections) {
			// TODO add exception handling
			con.sendNotification(notification);
		}
		// TODO 2. Broadcast notification to internal connectors
	}
	
	public void addConnection(BusConnection con) {
		connections.add(con);
	}
	
	public void removeConnection(BusConnection con) {
		if (connections.remove(con)==false) {
			log.warning("Couldn't remove BusConnection " + con + " from connection queue.");
		}
	}
	
// TODO implement InPushConnector support for internal notification

}
