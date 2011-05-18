package rsb;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.junit.Before;
import org.junit.Test;

import rsb.event.RSBDataListener;
import rsb.transport.TransportFactory;
import rsb.transport.spread.SpreadWrapper;

/**
 * User-level test for RSBJava.
 * 
 * @author jwienke
 */
public class UserLevelTest {

	@Test(timeout = 15000)
	public void roundtrip() throws Throwable {

//		Runnable r = new Runnable() {
//			
//			@Override
//			public void run() {
//				SpreadWrapper wrapper = new SpreadWrapper();
//				try {
//					wrapper.activate();
//				} catch (InitializeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				// wrapper.deactivate();
//			}
//		};
//		new Thread(r).start();
//		
//		Thread.sleep(30);
		
		LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
		for (Handler h : LogManager.getLogManager().getLogger("").getHandlers()) {
			h.setLevel(Level.FINEST);
		}

		final String scope = "/example/informer";

		// set up a receiver for events
		final Set<String> receivedMessages = new HashSet<String>();
		Subscriber sub = new Subscriber(scope, scope,
				TransportFactory.getInstance());
		sub.activate();
		sub.addListener(new RSBDataListener<String>() {

			@Override
			public void handleEvent(String d) {
				System.err.println("Received message '" + d + "'");
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
			System.err.println("Sent message '" + message + "' with hash "
					+ message.hashCode());
		}

		// wait for receiving all events that were sent
		synchronized (receivedMessages) {
			while (receivedMessages.size() < sentMessages.size()) {
				receivedMessages.wait();
			}
		}

		assertEquals(sentMessages, receivedMessages);

		System.err
				.println("************************************************************************************************");

		publisher.deactivate();
		sub.deactivate();

	}

//	@Before
//	public void XXXaaaa() throws Throwable {
//
//		SpreadWrapper wrapper = new SpreadWrapper();
//		wrapper.activate();
//		// wrapper.deactivate();
//
//	}
	
}
