package rsb.transport.spread;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rsb.Event;
import rsb.Id;
import rsb.QualityOfServiceSpec;
import rsb.Scope;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.transport.EventHandler;
import rsb.transport.convert.StringConverter;

/**
 * Test for {@link SpreadPort}.
 * 
 * @author jwienke
 */
@RunWith(value = Parameterized.class)
public class SpreadPortRoundtripTest {

	private int size;

	public SpreadPortRoundtripTest(int size) {
		this.size = size;
	}

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] { { 100 }, { 90000 }, { 110000 },
				{ 350000 } };
		return Arrays.asList(data);
	}

	@Test(timeout = 4000)
	public void roundtrip() throws Throwable {

		SpreadWrapper outWrapper = new SpreadWrapper();
		SpreadPort outPort = new SpreadPort(outWrapper, null);
		outPort.setQualityOfServiceSpec(new QualityOfServiceSpec(
				Ordering.ORDERED, Reliability.RELIABLE));
		outPort.addConverter("string", new StringConverter());

		final List<Event> receivedEvents = new ArrayList<Event>();
		SpreadWrapper inWrapper = new SpreadWrapper();
		SpreadPort inPort = new SpreadPort(inWrapper, new EventHandler() {

			@Override
			public void handle(Event e) {
				synchronized (receivedEvents) {
					receivedEvents.add(e);
					receivedEvents.notify();
				}
			}

		});
		inPort.addConverter("string", new StringConverter());

		inPort.activate();
		outPort.activate();

		final Scope scope = new Scope("/a/test/scope");
		inPort.notify(new ScopeFilter(scope), FilterAction.ADD);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			builder.append('c');
		}

		Event event = new Event("string");
		event.setId(new Id());
		event.setData(builder.toString());
		event.setScope(scope);
		event.getMetaData().setSenderId(new Id());
		event.getMetaData().setUserInfo("foo", "a long string");
		event.getMetaData().setUserInfo("barbar", "a long string again");
		event.getMetaData().setUserTime("asdasd", 324234);
		event.getMetaData().setUserTime("xxx", 42);

		outPort.push(event);

		synchronized (receivedEvents) {
			while (receivedEvents.size() != 1) {
				receivedEvents.wait();
			}

			Event receivedEvent = receivedEvents.get(0);

			// normalize times as they are not important for this test
			event.getMetaData().setReceiveTime(
					receivedEvent.getMetaData().getReceiveTime());

			assertEquals(event, receivedEvent);
		}

		inPort.deactivate();
		outPort.deactivate();

	}

}
