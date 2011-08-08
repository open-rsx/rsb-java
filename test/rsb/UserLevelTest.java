package rsb;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import rsb.converter.DefaultConverters;
import rsb.transport.TransportFactory;

/**
 * User-level test for RSBJava.
 * 
 * @author jwienke
 */
public class UserLevelTest {
	
	@Test(timeout = 15000)
	public void roundtrip() throws Throwable {

		// register converters
		DefaultConverters.register();
		
		final Scope scope = new Scope("/example/informer");

		// set up a receiver for events
		final Set<String> receivedMessages = new HashSet<String>();
		Listener listener = new Listener(scope, TransportFactory.getInstance());
		listener.activate();
		listener.addHandler(new AbstractEventHandler() {

			@Override
			public void handleEvent(Event e) {
				synchronized (receivedMessages) {
					receivedMessages.add((String) e.getData());
					receivedMessages.notify();
				}
			}
		}, true);

		// send events
		Set<String> sentMessages = new HashSet<String>();
		Informer<String> informer = new Informer<String>(scope);
		informer.activate();
		for (int i = 0; i < 100; ++i) {
			String message = "<message val=\"Hello World!\" nr=\"" + i + "\"/>";
			informer.send(message);
			sentMessages.add(message);
		}

		// wait for receiving all events that were sent
		synchronized (receivedMessages) {
			while (receivedMessages.size() < sentMessages.size()) {
				receivedMessages.wait();
			}
		}

		assertEquals(sentMessages, receivedMessages);

		informer.deactivate();
		listener.deactivate();

	}

}
