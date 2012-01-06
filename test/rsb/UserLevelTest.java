/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
