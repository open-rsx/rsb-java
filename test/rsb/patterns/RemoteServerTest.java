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
		return factory.createRemoteServer(new Scope("/example/server"));
	}

	@Test
	public void testActivate() throws InitializeException {
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.activate();
	}

	@Test
	public void testAddMethod() throws InitializeException {
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		remote.addMethod("callme");
		remote.activate();
		assertNotNull("Method not added to remote server",remote.getMethods().iterator().next());
	}	
	
	@Test
	public void testCallMethod() throws RSBException {
		final LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		server.addMethod("callme", new ReplyCallback());
		server.activate();
		final RemoteServer remote = getRemoteServer();
		assertNotNull("RemoteServer construction failed",remote);
		RemoteMethod<String, String> method = remote.addMethod("callme");
		assertNotNull(method);
		remote.activate();	
		String result = method.call("testdata");
		assertTrue("Received wrong result from server callback.",result.equals("testdata"));
	}
	
}
