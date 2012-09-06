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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class BusServer extends Bus implements Runnable {
	
	Logger log = Logger.getLogger(BusServer.class.getName());
	private ServerSocket server;
	
	public BusServer(InetAddress host, int port) throws IOException {
		server = new ServerSocket(55555); 
	}
	
	public void acceptClient() {
		// super.addConnection(newSocket);
	}

	@Override
	public void run() {
		try {

			System.out.println("Redirecting connections on port " + server.getLocalPort());// + " to " + newSite);

			while (true) {

				try {
					Socket s = server.accept();
					Thread t = new RedirectThread(s);
					t.start();
				} // end try
				catch (IOException e) {
				}

			} // end while

		} // end try
		catch (Exception e) {
			// TODO handle specific execeptions
		}
//		catch (BindException e) {
//			System.err.println("Could not start server. Port Occupied");
//		} catch (IOException e) {
//			System.err.println(e);
//		}

	}

}
