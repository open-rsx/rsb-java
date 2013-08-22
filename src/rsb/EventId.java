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
package rsb;

import java.util.Formatter;
import java.util.UUID;
import java.util.logging.Logger;

import rsb.util.UUIDTools;

/**
 * This class serves as a Uniform Resource Name to identify events in an RSB
 * system. This URN is based on the participant's ID and a sequence number
 * generated at the sender side unique for this participant. Both can be
 * combined and returned as a UUID. Please note, that the sequence number is a
 * 32bit unsigned integer and thus can overrun in a long-running system. In such
 * cases, the timestamp has to be additionally considered to further distinguish
 * between events.
 *
 * @author swrede
 * @author jwienke
 */
public class EventId {

    private static final Logger LOG = Logger.getLogger(EventId.class.getName());

    /**
     * ID of event generating participant.
     */
    private final ParticipantId participantId;

    /**
     * Sequence number unique within participant.
     */
    private final long sequenceNumber;

    /**
     * Unique ID.
     */
    private UUID uuid;

    /**
     * Creates a unique Id based on participant and sequence number.
     *
     * @param participantId
     *            the id of the participant causing this event
     * @param sequenceNumber
     *            a number from a sequential list of numbers of events
     *            originating from that participant
     */
    public EventId(final ParticipantId participantId, final long sequenceNumber) {
        this.participantId = participantId;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public int hashCode() {
        // CHECKSTYLE.OFF: AvoidInlineConditionals - more readable like this
        final int prime = 31;
        int result = 1;
        result *= prime;
        result +=
                this.participantId == null ? 0 : this.participantId.hashCode();
        result *= prime;
        result += (int) (this.sequenceNumber ^ (this.sequenceNumber >>> 32));
        result *= prime;
        result += this.uuid == null ? 0 : this.uuid.hashCode();
        return result;
        // CHECKSTYLE.ON: AvoidInlineConditionals
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final EventId other = (EventId) obj;
        if (this.participantId == null) {
            if (other.participantId != null) {
                return false;
            }
        } else if (!this.participantId.equals(other.participantId)) {
            return false;
        }
        if (this.sequenceNumber != other.sequenceNumber) {
            return false;
        }
        if (this.uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

    /**
     * Converts this {@link EventId} to a {@link UUID}.
     *
     * @return the UUID representation of this event id
     */
    public UUID getAsUUID() {
        if (this.uuid == null) {
            final String seqNr = formatSequenceNumber(this.sequenceNumber);
            LOG.finest("UUID generation for Event with ParticipantId "
                    + this.participantId.toString() + " and sequence number: "
                    + seqNr);
            this.uuid =
                    UUIDTools.getNameBasedUUID(this.participantId.getUUID(),
                            seqNr);
        }
        return this.uuid;
    }

    /**
     * Returns the id of the participant that sent the event.
     *
     * @return participant id
     */
    public ParticipantId getParticipantId() {
        return this.participantId;
    }

    /**
     * Returns the sequence number which makes this id unique combined with the
     * sending participants id.
     *
     * @return sequence number for sending participant
     * @see #getParticipantId()
     */
    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public static String formatSequenceNumber(final long value) {
        final StringBuilder builder = new StringBuilder();
        final Formatter formatter = new Formatter(builder);
        try {
            formatter.format("%08x", value);
            return builder.toString();
        } finally {
            formatter.close();
        }
    }

    @Override
    public String toString() {
        return "Id [participantId=" + this.participantId + ", sequenceNumber="
                + this.sequenceNumber + ", uuid=" + this.uuid + "]";
    }

}
