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
 * @author swrede
 * 
 */
public class EventBuilder {

    private static Logger log = Logger.getLogger(EventBuilder.class.getName());

    /**
     * Build event from RSB Notification. Excludes user data de-serialization as
     * it is bound to the converter configuration.
     * 
     * @param n
     *            {@link Notification} instance to deserialize
     * @return deserialized {@link Event} instance
     * 
     */
    public static Event fromNotification(final Notification n) {
        log.fine("decoding notification");
        final Event e = new Event();
        e.setScope(new Scope(n.getScope().toStringUtf8()));
        e.setId(new ParticipantId(n.getEventId().getSenderId().toByteArray()),
                n.getEventId().getSequenceNumber());
        if (n.hasMethod()) {
            e.setMethod(n.getMethod().toStringUtf8());
        }

        log.finest("returning event with id: " + e.getId());

        // metadata
        e.getMetaData().setCreateTime(n.getMetaData().getCreateTime());
        e.getMetaData().setSendTime(n.getMetaData().getSendTime());
        e.getMetaData().setReceiveTime(0);
        for (final UserInfo info : n.getMetaData().getUserInfosList()) {
            e.getMetaData().setUserInfo(info.getKey().toStringUtf8(),
                    info.getValue().toStringUtf8());
        }
        for (final UserTime time : n.getMetaData().getUserTimesList()) {
            e.getMetaData().setUserTime(time.getKey().toStringUtf8(),
                    time.getTimestamp());
        }

        // causes
        for (final EventId cause : n.getCausesList()) {
            e.addCause(new rsb.EventId(new ParticipantId(cause.getSenderId()
                    .toByteArray()), cause.getSequenceNumber()));
        }

        return e;
    }

}
