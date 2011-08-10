import java.util.logging.Logger;

import rsb.Event;
import rsb.Factory;
import rsb.InitializeException;
import rsb.Scope;
import rsb.patterns.DataCallback;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;


public class ServerExample {

	private static final Logger LOG = Logger.getLogger(ServerExample.class.getName());	
	
	public static class DataReplyCallback implements DataCallback<String, String> {

		@Override
		public String invoke(String request) throws Throwable {
			// do some stupid stuff
			return request + "reply";
		}
		
	}
	
	public static class EventReplyCallback implements EventCallback {

		@Override
		public Event invoke(Event request) throws Throwable {
			request.setData((String) request.getData() + "reply");
			return request;
		}
		
	}
	
	/**
	 * @param args
	 * @throws InitializeException 
	 */
	public static void main(String[] args) throws InitializeException {
		// get local server object which allows to expose request methods to participants
		LocalServer server = Factory.getInstance().createLocalServer(new Scope("/example/server"));
		server.activate();
		
		// add methods		
		// callback with handler signature based on event payload
		server.addMethod("replyData", new DataReplyCallback());
		// callback with handler signature based on events
		server.addMethod("replyEvent", new EventReplyCallback());

		// optional: block until server.deactivate or process shutdown
		LOG.info("Server /example/server running");
		server.waitForShutdown();
		
	}

}
