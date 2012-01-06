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

import rsb.Factory;
import rsb.Informer;

/**
 * An example how to use the {@link rsb.Informer} class to send events.
 * 
 * @author swrede
 */
public class InformerExample {

	public static void main(String[] args) throws Throwable {

		// Get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// Create an informer on scope "/exmaple/informer" to send event
		// notifications. 
		Informer<Object> informer = factory.createInformer("/example/informer");

		// Activate the informer to be ready for work
		informer.activate();

		// Send several events using a method that accepts the data and
		// automatically creates an appropriate event internally.
		for (int i = 1; i <= 1000; i++) {
			informer.send("<message val=\"Hello World!\" nr=\"" + i + "\"/>");
		}

		// As there is no explicit removal model in java, always manually
		// deactivate the informer if it is not needed anymore
		informer.deactivate();

	}

}
