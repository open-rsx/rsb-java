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
package rsb.transport.spread;

import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.UserData;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;
import rsb.transport.EventHandler;
import spread.SpreadException;
import spread.SpreadMessage;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * A task that continuously reads on a spread connection and decodes RSB
 * notifications from it.
 *
 * @author jwienke
 */
class ReceiverTask extends Thread {

	private Logger log = Logger.getLogger(ReceiverTask.class.getName());

	/**
	 * SpreadConnection
	 */
	private SpreadWrapper spread;

	private SpreadMessageConverter smc = new SpreadMessageConverter();

	private EventHandler eventHandler;

	private ConverterSelectionStrategy<ByteBuffer> converters;

	private AssemblyPool pool = new AssemblyPool();

	/**
	 * @param spreadWrapper
	 * @param converters
	 */
	ReceiverTask(SpreadWrapper spreadWrapper, EventHandler r,
			ConverterSelectionStrategy<ByteBuffer> converters) {
		this.spread = spreadWrapper;
		this.eventHandler = r;
		this.converters = converters;
	}

	public void run() {
		log.finer("Listener thread started");
		while (spread.conn.isConnected()
				&& !Thread.currentThread().isInterrupted()) {
			try {
				SpreadMessage sm = spread.conn.receive();
				log.fine("Message received from spread, message type: "
						+ sm.isRegular() + ", data = "
						+ new String(sm.getData()));
				// TODO check whether membership messages shall be handled
				// similar to data messages and be converted into events
				// TODO evaluate return value
				DataMessage dm = smc.process(sm);
				if (dm != null) {
					log.fine("Notification reveived by ReceiverTask");
					Event e = convertNotification(dm);
					if (e != null) {
						// dispatch event
						eventHandler.handle(e);
					}
				}
			} catch (InterruptedIOException e1) {
				log.info("Listener thread was interrupted during IO.");
				break;
			} catch (SpreadException e1) {
				if (!spread.conn.isConnected()) {
					log.fine("Spread connection is closed.");
				}
				if (!spread.shutdown) {
					log.warning("Caught a SpreadException while trying to receive a message: "
							+ e1.getMessage());
				}
				// get out here, stop this thread as no further messages can be retrieved
				// re-initialization elsewhere is necessary
				// TODO call error handler to allow framework shutdown from client-code
				this.interrupt();
			}
		}
		log.fine("Listener thread stopped");
	}

	// TODO think about whether this could actually be a regular converter call
	/**
	 * Method for converting Spread data messages into Java RSB events.
	 * The main purpose of this method is the conversion of Spread data
	 * messages by parsing the notification data structures as defined
	 * in RSB.Protocol using the ProtoBuf data holder classes.
	 */
	private Event convertNotification(DataMessage dm) {

		try {

			FragmentedNotification f = FragmentedNotification.parseFrom(dm.getData().array());
			AssemblyPool.DataAndNotification joinedData = pool.insert(f);

			if (joinedData != null) {
				Notification n = joinedData.getNotification();
				Event e = EventBuilder.fromNotification(n);
				
				// user data conversion
				// why not do this lazy after / in the filtering?
				// TODO deal with missing converters, errors
				Converter<ByteBuffer> c = converters.getConverter(n.getWireSchema().toStringUtf8());
				UserData<?> userData = c.deserialize(n.getWireSchema().toStringUtf8(), joinedData.getData());
				e.setData(userData.getData());
				e.setType(userData.getTypeInfo());

				return e;

			} else {
				return null;
			}

			// TODO better error handling with callback object
		} catch (InvalidProtocolBufferException e1) {
			log.log(Level.SEVERE, "Error decoding protocol buffer", e1);
			return null;
		} catch (ConversionException e1) {
			log.log(Level.SEVERE, "Error deserializing user data", e1);
			return null;
		}

	}
}
