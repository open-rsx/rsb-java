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
package rsb.transport.spread;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rsb.Event;
import rsb.EventId;
import rsb.ParticipantId;
import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.Scope;
import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.transport.EventHandler;

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
		UnambiguousConverterMap<ByteBuffer> inStrategy = new UnambiguousConverterMap<ByteBuffer>();
		inStrategy.addConverter("utf-8-string", new StringConverter());
		UnambiguousConverterMap<ByteBuffer> outStrategy = new UnambiguousConverterMap<ByteBuffer>();
		outStrategy.addConverter(String.class.getName(), new StringConverter());
		SpreadPort outPort = new SpreadPort(outWrapper, null, inStrategy,
				outStrategy);
		outPort.setQualityOfServiceSpec(new QualityOfServiceSpec(
				Ordering.ORDERED, Reliability.RELIABLE));

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

		}, inStrategy, outStrategy);

		inPort.activate();
		outPort.activate();

		final Scope scope = new Scope("/a/test/scope");
		inPort.notify(new ScopeFilter(scope), FilterAction.ADD);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			builder.append('c');
		}

		Event event = new Event(scope, String.class, builder.toString());
		event.setId(new ParticipantId(), 42);
		event.getMetaData().setUserInfo("foo", "a long string");
		event.getMetaData().setUserInfo("barbar", "a long string again");
		event.getMetaData().setUserTime("asdasd", 324234);
		event.getMetaData().setUserTime("xxx", 42);
		event.addCause(new EventId(new ParticipantId(), 23434));
		event.addCause(new EventId(new ParticipantId(), 42));

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
