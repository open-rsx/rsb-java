package tutorial.protobuf;
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

import java.util.concurrent.atomic.AtomicInteger;

import rsb.AbstractDataHandler;
import rsb.Factory;
import rsb.Listener;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import tutorial.protobuf.ImageMessage.SimpleImage;

/**
 * An example that demonstrated how to receive events in java.
 * 
 * @author swrede
 */
public class ListenerExample {

	static AtomicInteger counter = new AtomicInteger(0);
	static Object l = new Object();

	public static void main(String[] args) throws Throwable {

		// Instantiate generic ProtocolBufferConverter with SimpleImage exemplar
		ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());
		
		// register converter for SimpleImage's
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);		
		
		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create a Listener instance on the specified scope that will receive
		// events and dispatches them asynchronously to all registered handlers
		Listener sub = factory.createListener(new Scope("/example/informer"));

		// activate the listener to be ready for work
		sub.activate();

		// add a DataHandler that is notified directly with the data grabbed
		// from the received event
		sub.addHandler(new AbstractDataHandler<SimpleImage>() {

			@Override
			public void handleEvent(SimpleImage e) {
				try {
					counter.getAndIncrement();
					System.out.println("SimpleImage Data received: size=" + e.getData().size() + ", event # "
								+ counter.get());
					if (counter.get() == 100) {
						synchronized (l) {
							l.notifyAll();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}, true);

		// wait that enough events are received
		while (!allEventsDelivered()) {
			synchronized (l) {
				l.wait();
				System.out.println("Wake-Up!!!");
			}
		}

		// as there is no explicit removal model in java, always manually
		// deactivate the listener if it is not needed anymore
		sub.deactivate();
	};

	private synchronized static boolean allEventsDelivered() {
		return !(counter.get() != 100);
	}

}
