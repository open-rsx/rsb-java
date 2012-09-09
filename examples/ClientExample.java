/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
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
	 * @throws InitializeException
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException, InitializeException {
		// Get remote server object to call exposed request methods of participants
		RemoteServer server = Factory.getInstance().createRemoteServer("/example/server");
		server.activate();
		LOG.info("RemoteServer object activated");

		LOG.info("Calling remote server under scope /example/server:");
		try {
			LOG.info("Data-driven callback (replyHigher) synchronously: "
					+ server.call("replyHigher", "request"));
			LOG.info("Data-driven callback (replyHigher) with future: "
					+ server.callAsync("replyHigher", "request").get());
			Event event = new Event(String.class);
			event.setData("request");
			LOG.info("Event-driven callback (replyLower) synchronously: "
					+ server.call("replyLower", event.getData()));
			LOG.info("Event-driven callback (replyLower) with future: "
					+ server.callAsync("replyLower", event.getData()).get());
		} catch (RSBException e) {
			e.printStackTrace();
		}
		server.deactivate();
	}

}
