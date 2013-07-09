/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
package rsb.transport;

import java.util.logging.Logger;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.NotificationType.Notification;

/**
 * A utility class to construct {@link Event}s from {@link Notification}s.
 * 
 * @author swrede
 */
public final class EventBuilder {

    private static final Logger LOG = Logger.getLogger(EventBuilder.class
            .getName());

    private EventBuilder() {
        // prevent initialization of utility class
        super();
    }

    /**
     * Build event from RSB Notification. Excludes user data de-serialization as
     * it is bound to the converter configuration.
     * 
     * @param notification
     *            {@link Notification} instance to deserialize
     * @return deserialized {@link Event} instance
     */
    public static Event fromNotification(final Notification notification) {
        LOG.fine("decoding notification");
        final Event event = new Event();
        event.setScope(new Scope(notification.getScope().toStringUtf8()));
        event.setId(new ParticipantId(notification.getEventId().getSenderId()
                .toByteArray()), notification.getEventId().getSequenceNumber());
        if (notification.hasMethod()) {
            event.setMethod(notification.getMethod().toStringUtf8());
        }

        LOG.finest("returning event with id: " + event.getId());

        // metadata
        event.getMetaData().setCreateTime(
                notification.getMetaData().getCreateTime());
        event.getMetaData().setSendTime(
                notification.getMetaData().getSendTime());
        event.getMetaData().setReceiveTime(0);
        for (final UserInfo info : notification.getMetaData()
                .getUserInfosList()) {
            event.getMetaData().setUserInfo(info.getKey().toStringUtf8(),
                    info.getValue().toStringUtf8());
        }
        for (final UserTime time : notification.getMetaData()
                .getUserTimesList()) {
            event.getMetaData().setUserTime(time.getKey().toStringUtf8(),
                    time.getTimestamp());
        }

        // causes
        for (final EventId cause : notification.getCausesList()) {
            event.addCause(new rsb.EventId(new ParticipantId(cause // NOPMD
                    .getSenderId().toByteArray()), cause.getSequenceNumber()));
        }

        return event;
    }

}
