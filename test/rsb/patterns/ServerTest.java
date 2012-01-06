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
package rsb.patterns;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import rsb.Factory;
import rsb.InitializeException;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ServerTest {

	final static private Logger LOG = Logger.getLogger(ServerTest.class.getName());
	
	public class ShutdownCallback implements DataCallback<String,String> {

		Server server;
		
		public ShutdownCallback(Server server) {
			this.server = server;
		}
		
		@Override
		public String invoke(String request) throws Throwable {
			server.deactivate();
			return "shutdown now";
		}
					
	}
	
	/**
	 * Test method for {@link rsb.patterns.Server#Server(rsb.Scope, rsb.transport.TransportFactory, rsb.transport.PortConfiguration)}.
	 */
	@Test
	public void testServer() {
		Server server = getServer();
		assertNotNull(server);
	}

	private Server getServer() {
		Factory factory = Factory.getInstance();
		Server server = factory.createLocalServer(new Scope("/example/server"));
		return server;
	}

	/**
	 * Test method for {@link rsb.patterns.Server#getMethods()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testGetMethods() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		assertTrue(server.getMethods().size()==0);
		server.addMethod("callme", new ReplyDataCallback());
		assertTrue(server.getMethods().size()==1);
		assertTrue(server.getMethods().iterator().next().getName().equals("callme"));
	}

	@Test
	public void addMethod() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		server.addMethod("callme", new ReplyDataCallback());
		server.addMethod("callmeEvent", new ReplyEventCallback());
		assertTrue(server.getMethods().size()==2);
	}
	
	/**
	 * Test method for {@link rsb.patterns.Server#activate()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testActivate() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		assertFalse(server.isActive());
		server.activate();
		assertTrue(server.isActive());
		server.deactivate();
		assertFalse(server.isActive());
		server.addMethod("callme", new ReplyDataCallback());
		server.activate();
		server.deactivate();
	}

	/**
	 * Test method for {@link rsb.patterns.Server#deactivate()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		DataCallback<String, String> method = new ReplyDataCallback();
		server.addMethod("callme", method);
		server.activate();
		assertTrue(server.isActive());
		assertTrue(server.getMethods().iterator().next().isActive());
		server.deactivate();
		assertFalse(server.isActive());
		assertFalse(server.getMethods().iterator().next().isActive());
	}
	
	@Test 
	public void testStartServer() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		DataCallback<String, String> method = new ReplyDataCallback();
		server.addMethod("callme", method);
		server.activate();		
		server.addMethod("callmetoo", method);
		server.deactivate();
	}

	@Test
	public void testBlocking() throws InitializeException {
		final LocalServer server = (LocalServer) getServer();
		DataCallback<String, String> method = new ShutdownCallback(server);
		server.addMethod("shutdown", method);
		server.activate();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// must not happen
				}
				LOG.info("Shutting down server from callback.");
				server.deactivate();
				
			}
		});
		LOG.info("Server running, shutting down in 500ms.");
		t.run();
		server.waitForShutdown();
	}
	
}
