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
package rsb.patterns;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;

public class RemoteServerTest {
	
	public final static Logger log = Logger.getLogger(RemoteServerTest.class.getCanonicalName());
	
	@Test
	public void testRemoteServerScopeDouble() {
		final Factory factory = Factory.getInstance();
		final RemoteServer remote = factory.createRemoteServer(new Scope("/example/server"),10);
		assertNotNull("RemoteServer construction failed",remote);
		assertTrue("Timeout not resepected upon construction",remote.getTimeout()==10);
	}

	@Test
	public void testRemoteServerScope() {
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
	}

	private RemoteServer getRemoteServer() {
		final Factory factory = Factory.getInstance();
		return factory.createRemoteServer(new Scope("/example/server"),1200);
	}

	@Test
	public void testActivate() throws InitializeException {
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();
		remote.deactivate();
	}

	@Test
	public void testAddMethod() throws InitializeException {
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.addMethod("callme",true);
		remote.activate();
		assertNotNull("Method not added to remote server",remote.getMethods().iterator().next());
		remote.deactivate();
	}

	@Test
	public void testCallMethod() throws RSBException, InterruptedException, ExecutionException {
		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		ReplyDataCallback dataCallback = new ReplyDataCallback();
		ReplyEventCallback eventCallback = new ReplyEventCallback();
		server.addMethod("callme", dataCallback);
		server.addMethod("callme2", eventCallback);
		server.activate();
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();
		String result1 = null;
		String result2 = null;
		List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
		List<Future<String>> resultsData = new ArrayList<Future<String>>();
		for (int i = 0; i < 100; i++) {
			result1 = remote.call("callme","testdata");
			Event event = new Event(String.class);
			event.setData("testdata2");
			Event event2 = remote.call("callme2",event);
			result2 = (String) event2.getData();
			Event event3 = new Event(String.class);
			event3.setData("testdata2");
			resultsEvents.add(remote.callAsync("callme2", event3));
			Future<String> future = remote.callAsync("callme", "testdata");
			resultsData.add(future);
		}
		// TODO make this test nicer, remove sleep...
		Thread.sleep(500);
		// check result of blocking call
		assertEquals("Incorrect number of event callback invocations!",200,dataCallback.counter.get());
		assertEquals("Incorrect number of data callback invocations!",200,eventCallback.counter.get());
		assertTrue("Received wrong result from server callback.",result1.equals("testdata"));
		assertTrue("Received wrong result from server callback.",result2.equals("testdata2"));
		assertEquals("Not all events delivererd!",100, resultsEvents.size());
		assertEquals("Not all data delivererd!",100, resultsData.size());
		// check result of async event calls
		for (int i = 0; i < 100; i++) {
			String result = (String) resultsEvents.get(i).get().getData();
			assertTrue("Received wrong result from server callback.",result.equals("testdata2"));
		}
		// check result of async data calls
		for (int i = 0; i < 100; i++) {
			String result = resultsData.get(i).get();
			assertTrue("Received wrong result from server callback.",result.equals("testdata"));
		}
	}
	
//	@Test
//	public void testCallMethodBlindRPC() throws RSBException, InterruptedException, ExecutionException {
//		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
//		ReplyDataCallback dataCallback = new ReplyDataCallback();
//		ReplyEventCallback eventCallback = new ReplyEventCallback();
//		// TODO new type of callback needed?
//		server.addMethod("callme", dataCallback);
//		server.addMethod("callme2", eventCallback);
//		server.activate();
//		final RemoteServer remote = getRemoteServer();
//		assertNotNull("RemoteServer construction failed",remote);
//		remote.activate();
//		String result1 = null;
//		String result2 = null;
//		for (int i = 0; i < 100; i++) {
//			// TODO what about return the sent event? I think we should do that?!
//			remote.call("callme","testdata");
//			Event event = new Event(String.class);
//			event.setData("testdata2");
//			Event event2 = remote.call("callme2",event);
//			result2 = (String) event2.getData();
//			Event event3 = new Event(String.class);
//			event3.setData("testdata2");
//			//remote.callAsync("callme2", event3);
//			// TODO hwo to get an exception when no result-object is available?
//			// TODO construct some kind of status future?
//			// TODO first start with non-async case
//			Future<String> future = remote.callAsync("callme", "testdata");
//		}
//		// TODO make this test nicer, remove sleep...
//		Thread.sleep(500);
//		// check result of blocking call
//		assertEquals("Incorrect number of event callback invocations!",200,dataCallback.counter.get());
//		assertEquals("Incorrect number of data callback invocations!",200,eventCallback.counter.get());
//		assertTrue("Received wrong result from server callback.",result1.equals("testdata"));
//		assertTrue("Received wrong result from server callback.",result2.equals("testdata2"));
//		// TODO check result of async calls
//	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCallMethodWithoutParameter() throws RSBException {
		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		DataInCallback dataCallback = new DataInCallback();
		EventInCallback eventCallback = new EventInCallback();
		List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
		// TODO new type of callback needed?
		server.addMethod("callme", dataCallback);
		server.addMethod("callme2", eventCallback);
		log.info("prepared server");
		server.activate();
		log.info("activated server");
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();

		for (int i = 0; i < 100; i++) {
			// optional use of reply value is possible
			Event event = remote.call("callme");
			// but can also be ignored
			remote.call("callme2");
			// asynchronous completion
			resultsEvents.add((Future<Event>) remote.callAsync("callme2"));
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO check result of blocking call
		assertEquals("Incorrect number of data callback invocations!",100,dataCallback.counter.get());
		assertEquals("Incorrect number of eve t callback invocations!",200,eventCallback.counter.get());		
		// TODO check result of async calls
	}			
}
