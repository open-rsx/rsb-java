/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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

import rsb.EventId;
import rsb.ParticipantId;
import rsb.protocol.FragmentedNotificationType;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType;

import com.google.protobuf.ByteString;

/**
 * A class that assembles fragmented messages received over spread in form of
 * {@link rsb.protocol.NotificationType.Notification}s.
 *
 * @author jwienke
 */
public class AssemblyPool {

    private final Map<EventId, Assembly> assemblies =
            new HashMap<EventId, Assembly>();

    /**
     * Assembles a fragmented notification.
     *
     * @author jwienke
     */
    private class Assembly {

        private final Map<Integer, FragmentedNotification> notifications =
                new HashMap<Integer, FragmentedNotificationType.FragmentedNotification>();
        private final int requiredParts;

        public Assembly(
                @SuppressWarnings("PMD.LongVariable") final FragmentedNotification initialNotification) {
            assert initialNotification.getNumDataParts() > 1;
            this.notifications.put(initialNotification.getDataPart(),
                    initialNotification);
            this.requiredParts = initialNotification.getNumDataParts();
        }

        public FragmentedNotification getInitialFragment() {
            return this.notifications.get(0);
        }

        public ByteBuffer add(final FragmentedNotification notification) {
            assert !this.notifications.containsKey(notification.getDataPart());
            this.notifications.put(notification.getDataPart(), notification);

            if (this.notifications.size() == this.requiredParts) {

                final ByteArrayOutputStream stream =
                        new ByteArrayOutputStream();
                for (int part = 0; part < this.requiredParts; ++part) {
                    assert this.notifications.containsKey(part);

                    final ByteString currentData =
                            this.notifications.get(part).getNotification()
                                    .getData();
                    stream.write(currentData.toByteArray(), 0,
                            currentData.size());

                }
                return ByteBuffer.wrap(stream.toByteArray());

            } else {
                return null;
            }

        }

    }

    /**
     * Collects the raw data from the wire as well as a notification causing
     * them.
     */
    public class DataAndNotification {

        private final ByteBuffer data;
        private final NotificationType.Notification notification;

        /**
         * Constructor.
         *
         * @param data
         *            data from the wire
         * @param notification
         *            causing notification for the data
         */
        public DataAndNotification(final ByteBuffer data,
                final NotificationType.Notification notification) {
            this.data = data;
            this.notification = notification;
        }

        /**
         * Returns the wire data.
         *
         * @return data
         */
        public ByteBuffer getData() {
            return this.data;
        }

        /**
         * Returns the causing notification for the data.
         *
         * @return notification
         */
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
    public
            DataAndNotification
            insert(final FragmentedNotificationType.FragmentedNotification notification) {

        assert notification.getNumDataParts() > 0;
        if (notification.getNumDataParts() == 1) {
            return new DataAndNotification(ByteBuffer.wrap(notification
                    .getNotification().getData().toByteArray()),
                    notification.getNotification());
        }

        @SuppressWarnings("PMD.ShortVariable")
        final EventId id =
                new EventId(new ParticipantId(notification.getNotification()
                        .getEventId().getSenderId().toByteArray()),
                        notification.getNotification().getEventId()
                                .getSequenceNumber());
        if (!this.assemblies.containsKey(id)) {
            this.assemblies.put(id, new Assembly(notification));
            return null;
        }

        final Assembly assembly = this.assemblies.get(id);
        assert assembly != null;
        final ByteBuffer joinedData = assembly.add(notification);
        if (joinedData == null) {
            return null;
        } else {
            this.assemblies.remove(id);
            return new DataAndNotification(joinedData, assembly
                    .getInitialFragment().getNotification());
        }
    }

}
