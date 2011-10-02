/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Factory;
import rsb.RSBException;
import rsb.patterns.RemoteServer;

/**
 * This class demonstrates how to access an RSB server
 * object using synchronous and asynchronously calls.
 * 
 * @author swrede
 * 
 */
public class ClientExample {

	private static final Logger LOG = Logger.getLogger(ClientExample.class.getName());
	
	/**
	 * @param args
	 * @throws RSBException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws RSBException, InterruptedException, ExecutionException {
		// Get remote server object to call exposed request methods of participants
		RemoteServer server = Factory.getInstance().createRemoteServer("/example/server");
		server.activate();
		LOG.info("RemoteServer object activated");

		LOG.info("Calling remote server under scope /example/server:");
		LOG.info("Data-driven callback (replyHigher) synchronously: " + server.call("replyHigher", "request"));
		LOG.info("Data-driven callback (replyHigher) with future: " + server.callAsync("replyHigher", "request").get());
		Event event = new Event(String.class);
		event.setData("request");
		LOG.info("Event-driven callback (replyLower) synchronously: " + server.call("replyLower", event.getData()));
		LOG.info("Event-driven callback (replyLower) with future: " + server.callAsync("replyLower", event.getData()).get());		
		
		server.deactivate();
	}

}
