/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
