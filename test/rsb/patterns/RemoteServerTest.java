package rsb.patterns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.RSBException;
import rsb.Scope;

public class RemoteServerTest {

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
			resultsData.add(remote.callAsync("callme", "testdata"));
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
	
}
