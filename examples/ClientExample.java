import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import rsb.Factory;
import rsb.RSBException;
import rsb.Scope;
import rsb.patterns.RemoteServer;

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

/**
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
		// get remote server object to call exposed request methods of participants
		RemoteServer server = Factory.getInstance().createRemoteServer(new Scope("/example/server"));
		server.activate();
		LOG.info("RemoteServer object activated");

		LOG.info("Calling remote server under scope /example/server:");
		LOG.info("Data signature (replyData) synchronously: " + server.call("replyData", "request"));
		LOG.info("Data signature (replyData) with future: " + server.callAsync("replyData", "request").get());
		LOG.info("Event signature (replyEvent) synchronously: " + server.call("replyEvent", "request"));
		LOG.info("Event signature (replyEvent) with future: " + server.callAsync("replyEvent", "request").get());		
		
		server.deactivate();
	}

}
