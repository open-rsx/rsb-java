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

import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;

public class RemoteServerTest {
	
	public final static Logger log = Logger.getLogger(RemoteServerTest.class.getCanonicalName());
	
	DataInCallback dataInCallback = new DataInCallback();
	EventInCallback eventInCallback = new EventInCallback();
	ReplyDataCallback dataCallback = new ReplyDataCallback();
	ReplyEventCallback eventCallback = new ReplyEventCallback();
	
	@Before
	public void resetCounters() {
		dataCallback.counter.set(0);
		eventCallback.counter.set(0);
		dataInCallback.counter.set(0);
		eventInCallback.counter.set(0);
	}
	
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
		final RemoteServer remote = setupActiveObjects();
		assertNotNull("RemoteServer construction failed",remote);
		String result1 = null;
		String result2 = null;
		List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
		List<Future<String>> resultsData = new ArrayList<Future<String>>();
		for (int i = 0; i < 100; i++) {
			result1 = remote.call("callmedc","testdata");
			Event event = new Event(String.class);
			event.setData("testdata2");
			Event event2 = remote.call("callmeec",event);
			result2 = (String) event2.getData();
			Event event3 = new Event(String.class);
			event3.setData("testdata2");
			resultsEvents.add(remote.callAsync("callmeec", event3));
			Future<String> future = remote.callAsync("callmedc", "testdata");
			resultsData.add(future);
		}
		// TODO make this test nicer, remove sleep...
		Thread.sleep(500);
		// check result of blocking call
		result1.equals("adf");
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

	@SuppressWarnings("unchecked")
	private RemoteServer setupActiveObjects() throws RSBException {
		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		// TODO new type of callback needed?
		server.addMethod("callmedi", dataInCallback);
		server.addMethod("callmeei", eventInCallback);
		server.addMethod("callmedc", dataCallback);
		server.addMethod("callmeec", eventCallback);	
		
		log.info("prepared server");
		server.activate();
		log.info("activated server");
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();
		return remote;
	}
	
	@Test
	public void testCallMethodWithoutParameter() throws RSBException {
		List<Future<Event>> resultsEvents = new ArrayList<Future<Event>>();
		RemoteServer remote = setupActiveObjects();
		for (int i = 0; i < 100; i++) {
			// optional use of reply value is possible
			Event event = remote.call("callmedi");
			// but can also be ignored
			remote.call("callmeei");
			// asynchronous completion
			resultsEvents.add((Future<Event>) remote.callAsync("callmeei"));
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO check result of blocking call
		assertEquals("Incorrect number of data callback invocations!",100,dataInCallback.counter.get());
		assertEquals("Incorrect number of event callback invocations!",200,eventInCallback.counter.get());		
		// TODO check result of async calls
	}	

}
