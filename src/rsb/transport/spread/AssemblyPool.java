/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;


import rsb.EventId;
import rsb.ParticipantId;
import rsb.protocol.NotificationType;
import rsb.protocol.FragmentedNotificationType;

/**
 * A class that assembles fragmented messages received over spread in form of
 * {@link Notification}s.
 *
 * @author jwienke
 */
public class AssemblyPool {

	/**
	 * Assembles a fragmented notification.
	 *
	 * @author jwienke
	 */
	private class Assembly {

		private Map<Integer, FragmentedNotificationType.FragmentedNotification> notifications
		    = new HashMap<Integer, FragmentedNotificationType.FragmentedNotification>();
		private int requiredParts = 0;

		public Assembly(FragmentedNotificationType.FragmentedNotification initialNotification) {
			assert (initialNotification.getNumDataParts() > 1);
			notifications.put(initialNotification.getDataPart(),
					initialNotification);
			requiredParts = initialNotification.getNumDataParts();
		}

		public FragmentedNotificationType.FragmentedNotification getInitialFragment() {
			return notifications.get(0);
		}

		public ByteBuffer add(FragmentedNotificationType.FragmentedNotification notification) {
			assert !notifications.containsKey(notification.getDataPart());
			notifications.put(notification.getDataPart(), notification);

			if (notifications.size() == requiredParts) {

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				for (int part = 0; part < requiredParts; ++part) {
					assert notifications.containsKey(part);

					ByteString currentData
					    = notifications.get(part).getNotification().getData();
					stream.write(currentData.toByteArray(), 0,
							currentData.size());

				}
				return ByteBuffer.wrap(stream.toByteArray());

			} else {
				return null;
			}

		}

	}

	private Map<EventId, Assembly> assemblies = new HashMap<EventId, Assembly>();

        public class DataAndNotification {
		private ByteBuffer		      data;
		private NotificationType.Notification notification;

		public DataAndNotification(ByteBuffer		         data,
					   NotificationType.Notification notification) {
			this.data	  = data;
			this.notification = notification;
		}

		public ByteBuffer getData() {
			return this.data;
		}

		public NotificationType.Notification getNotification() {
			return this.notification;
		}
        };

	/**
	 * Adds a new message to the assembly pool and joins the data of all
	 * notifications of the same event, if all fragments were received.
	 *
	 * @param notification
	 *            newly received notification
	 * @return joined data or <code>null</code> if not event was completed with
	 *         this notification
	 */
	public DataAndNotification insert(FragmentedNotificationType.FragmentedNotification notification) {

		assert notification.getNumDataParts() > 0;
		if (notification.getNumDataParts() == 1) {
		        return new DataAndNotification(ByteBuffer.wrap(notification.getNotification().getData().toByteArray()),
						       notification.getNotification());
		}

		EventId id = new EventId(new ParticipantId(notification.getNotification().getEventId().getSenderId().toByteArray()),
					 notification.getNotification().getEventId().getSequenceNumber());
		if (!assemblies.containsKey(id)) {
			assemblies.put(id, new Assembly(notification));
			return null;
		}

		Assembly assembly = assemblies.get(id);
		assert assembly != null;
		ByteBuffer joinedData = assembly.add(notification);
		if (joinedData != null) {
			assemblies.remove(id);
			return new DataAndNotification(joinedData, assembly.getInitialFragment().getNotification());
		} else {
			return null;
		}
	}

}
