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

import java.util.HashSet;
import java.util.Set;

/**
 * Basic event structure exchanged between RSB ports. It is a combination of
 * metadata and the actual data to publish / subscribe to as payload.
 * 
 * Events are often caused by other events, which e.g. means that their
 * contained payload was calculated on the payload of one or more other events.
 * 
 * To express these relations each event contains a set of EventIds that express
 * the direct causes of the event. This means, transitive event causes are not
 * modeled.
 * 
 * Cause handling is inspired by the ideas proposed in: David Luckham, The Power
 * of Events, Addison-Wessley, 2007
 * 
 * @author swrede
 */
// TODO check if we want to provide the type via a template parameter
// Rationale: A single event can only hold a single type
public class Event {

    @SuppressWarnings("PMD.ShortVariable")
    private EventId id = null;
    private Class<?> type = Object.class;
    private Scope scope;
    private String method;
    private Object data;
    private final MetaData metaData = new MetaData();

    /**
     * The causes of one event as a set of causing IDs.
     */
    private final Set<EventId> causes = new HashSet<EventId>();

    // TODO move event creation into factory?

    /**
     * Creates a new event that can be send to scope.
     * 
     * @param scope
     *            The scope to which the event will be sent.
     * @param type
     *            A class object indicating the class of the data being sent in
     *            the event.
     * @param data
     *            The actual data that should be sent in the event.
     */
    public Event(final Scope scope, final Class<?> type, final Object data) {
        this(type, data);
        this.scope = scope;
    }

    public Event(final Class<?> type) {
        this.setType(type);
    }

    public Event(final Class<?> type, final Object data) {
        this(type);
        this.data = data;
    }

    /**
     * Construct empty event. Only metadata is initialized.
     */
    public Event() {
        // meta data is initialized in the filed declaration already0
    }

    /**
     * @return the Java type of the payload, may be <code>null</code> to express
     *         a void type where no payload is carried at all.
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * @param type
     *            the Java type to set for the Event payload
     */
    public void setType(final Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Event types must be class instances. For no data use Void.class.");
        }
        this.type = type;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return this.data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * @return the scope
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Sets all information necessary to generate the {@link EventId} of this
     * event. After this call {@link #getId()} is able to return an id.
     * 
     * @param senderId
     *            id of the sending participant for this event
     * @param sequenceNumber
     *            sequence number within the specified participant
     */
    public void setId(final ParticipantId senderId, final long sequenceNumber) {
        this.id = new EventId(senderId, sequenceNumber);
    }

    /**
     * Sets the id of this event. Afterwards {@link #getId()} can return an id.
     * 
     * @param id
     *            new id to set
     */
    @SuppressWarnings("PMD.ShortVariable")
    public void setId(final EventId id) {
        this.id = id;
    }

    /**
     * Returns the id of the sending participant for this event.
     * 
     * @return sending participant id
     * @throws IllegalStateException
     *             the id is not yet defined because the event was not sent by
     *             an {@link Informer} so far
     * @deprecated use {@link #getId()} instead
     */
    @Deprecated
    public ParticipantId getSenderId() {
        return this.getId().getParticipantId();
    }

    /**
     * @param scope
     *            the scope to set
     */
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    /**
     * Returns the sequence number of the informer that sent the event.
     * 
     * @return unique number within one informer that sends an event
     * @throws IllegalStateException
     *             the id is not yet defined because the event was not sent by
     *             an {@link Informer} so far
     * @deprecated use {@link #getId()} instead
     */
    @Deprecated
    public long getSequenceNumber() {
        return this.getId().getSequenceNumber();
    }

    public EventId getId() {
        if (this.id == null) {
            throw new IllegalStateException(
                    "The id of this event is not defined yet, "
                            + "because it was not sent by an informer so far.");
        }
        return this.id;
    }

    /**
     * Returns a {@link MetaData} instance representing the meta data for this
     * event.
     * 
     * @return meta data of this event, not <code>null</code>
     */
    public MetaData getMetaData() {
        return this.metaData;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Event[id=" + this.getId() + ", scope=" + this.scope + ", type="
                + this.type + ", metaData=" + this.metaData + ", causes="
                + this.causes.toString() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.data == null) ? 0 : this.data.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result
                + ((this.metaData == null) ? 0 : this.metaData.hashCode());
        result = prime * result
                + ((this.method == null) ? 0 : this.method.hashCode());
        result = prime * result
                + ((this.scope == null) ? 0 : this.scope.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + this.type.hashCode();
        result = prime * result + this.causes.hashCode();
        return result;
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
        final Event other = (Event) obj;
        if (this.data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!this.data.equals(other.data)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.metaData == null) {
            if (other.metaData != null) {
                return false;
            }
        } else if (!this.metaData.equals(other.metaData)) {
            return false;
        }
        if (this.method == null) {
            if (other.method != null) {
                return false;
            }
        } else if (!this.method.equalsIgnoreCase(other.method)) {
            return false;
        }
        if (this.scope == null) {
            if (other.scope != null) {
                return false;
            }
        } else if (!this.scope.equals(other.scope)) {
            return false;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (!this.causes.equals(other.causes)) {
            return false;
        }
        return true;
    }

    /**
     * Adds the id of one event to the causes of this event. If the set of
     * causing events already contained the given id, this call has no effect.
     * 
     * @param id
     *            the id of a causing event
     * @return <code>true</code> if the causes was added, <code>false</code> if
     *         it already existed
     */
    @SuppressWarnings("PMD.ShortVariable")
    public boolean addCause(final EventId id) {
        return this.causes.add(id);
    }

    /**
     * Removes a causing event from the set of causes for this event. If the id
     * was not contained in this set, the call has no effect.
     * 
     * @param id
     *            of the causing event
     * @return <code>true</code> if an event with this id was removed from the
     *         causes, else <code>false</code>
     */
    @SuppressWarnings("PMD.ShortVariable")
    public boolean removeCause(final EventId id) {
        return this.causes.remove(id);
    }

    /**
     * Tells whether the id of one event is already marked as a cause of this
     * event.
     * 
     * @param id
     *            id of the event to test causality for
     * @return <code>true</code> if id is marked as a cause for this event, else
     *         <code>false</code>
     */
    @SuppressWarnings("PMD.ShortVariable")
    public boolean isCause(final EventId id) {
        return this.causes.contains(id);
    }

    /**
     * Returns all causing events marked so far.
     * 
     * @return set of causing event ids. Modifications to this set do not affect
     *         this event as it is a copy.
     */
    public Set<EventId> getCauses() {
        return new HashSet<EventId>(this.causes);
    }

}
