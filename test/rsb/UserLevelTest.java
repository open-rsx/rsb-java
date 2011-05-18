package rsb;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import rsb.event.RSBDataListener;
import rsb.transport.TransportFactory;

/**
 * User-level test for RSBJava.
 * 
 * @author jwienke
 */
public class UserLevelTest {

	@Test(timeout = 30000)
	public void roundtrip() throws Throwable {

		final String scope = "/example/informer";

		// set up a receiver for events
		final Set<String> receivedMessages = new HashSet<String>();
		Subscriber sub = new Subscriber(scope, scope,
				TransportFactory.getInstance());
		sub.activate();
		sub.addListener(new RSBDataListener<String>() {

			@Override
			public void handleEvent(String d) {
				System.out.println("Received message '" + d + "'");
				synchronized (receivedMessages) {
					receivedMessages.add(d);
					receivedMessages.notify();
				}
			}
		});

		// send events
		Set<String> sentMessages = new HashSet<String>();
		Publisher<String> publisher = new Publisher<String>(scope);
		publisher.activate();
		for (int i = 0; i < 100; ++i) {
			String message = "<message val=\"Hello World!\" nr=\"" + i + "\"/>";
			publisher.send(message);
			sentMessages.add(message);
			System.out.println("Sent message '" + message + "' with hash "
					+ message.hashCode());
		}
		publisher.deactivate();

		// wait for receiving all events that were sent
		synchronized (receivedMessages) {
			while (receivedMessages.size() < sentMessages.size()) {
				receivedMessages.wait();
			}
		}

		assertEquals(sentMessages, receivedMessages);

	}

}
