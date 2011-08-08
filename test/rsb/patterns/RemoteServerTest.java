package rsb.patterns;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		remote.addMethod("callme");
		remote.activate();
		assertNotNull("Method not added to remote server",remote.getMethods().iterator().next());
		remote.deactivate();
	}	
	
	@Test
	public void testCallMethod() throws RSBException {
		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		ReplyCallback cb = new ReplyCallback();
		server.addMethod("callme", cb);
		server.activate();
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();	
		String result = null;
		for (int i = 0; i < 100; i++) {
			result = remote.call("callme","testdata");
		}
		System.out.println("Count requests: " + cb.counter.get());
		System.out.println("Listener: " + remote.getMethods().iterator().next().listener.getHandlers());
		assertTrue("Received wrong result from server callback.",result.equals("testdata"));
	}
	
}
