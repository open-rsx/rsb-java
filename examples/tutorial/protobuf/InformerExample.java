package tutorial.protobuf;
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

import com.google.protobuf.ByteString;

import rsb.Factory;
import rsb.Informer;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import tutorial.protobuf.ImageMessage.SimpleImage;
import tutorial.protobuf.ImageMessage.SimpleImage.Builder;

/**
 * An example how to use the {@link rsb.Informer} class to send events.
 *
 * @author swrede
 */
public class InformerExample {

	public static void main(String[] args) throws Throwable {

		// Instantiate generic ProtocolBufferConverter with SimpleImage exemplar
		ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());

		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// register converter for SimpleImage's
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);

		// create an informer on scope "/example/informer" to send event
		// notifications. This informer is capable of sending Strings.
		Informer<SimpleImage> informer = factory.createInformer(new Scope(
				"/example/informer"));

		// activate the informer to be ready for work
		informer.activate();

		// send several events using a method that accepts the data and
		// automatically creates an appropriate event internally.
		for (int i = 0; i < 100; i++) {
			Builder img = SimpleImage.newBuilder();
			img.setHeight(100);
			img.setWidth(100);
			byte[] bytes = new byte[100*100];
			ByteString bs = ByteString.copyFrom(bytes);
			img.setData(bs);
			informer.send(img.build());
			System.out.println("Sending image...");
		}

		// as there is no explicit removal model in java, always manually
		// deactivate the informer if it is not needed anymore
		informer.deactivate();

	}

}
