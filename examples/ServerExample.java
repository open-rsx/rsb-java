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

import java.util.logging.Logger;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.patterns.DataCallback;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;

/**
 * This example demonstrates how to expose a request/reply interface
 * with RSB using data and event callbacks.
 * 
 * @author swrede
 *
 */
public class ServerExample {

	private static final Logger LOG = Logger.getLogger(ServerExample.class.getName());	
	
	public static class DataReplyCallback implements DataCallback<String, String> {

		@Override
		public String invoke(String request) throws Throwable {
			// do some stupid stuff
			return (request + "/reply").toLowerCase();
		}
		
	}
	
	public static class EventReplyCallback implements EventCallback {

		@Override
		public Event invoke(Event request) throws Throwable {
			request.setData(((String) request.getData()) + "/reply".toUpperCase());
			return request;
		}
		
	}
	
	/**
	 * @param args
	 * @throws InitializeException 
	 */
	public static void main(String[] args) throws InitializeException {
		// Get local server object which allows to expose request methods to participants
		LocalServer server = Factory.getInstance().createLocalServer("/example/server");
		server.activate();
		
		// Add methods		
		// Callback with handler signature based on event payload
		server.addMethod("replyLower", new DataReplyCallback());
		// Callback with handler signature based on events
		server.addMethod("replyHigher", new EventReplyCallback());

		// Optional: block until server.deactivate or process shutdown
		LOG.info("Server /example/server running");
		server.waitForShutdown();
		
	}

}
