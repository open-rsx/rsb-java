/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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

	private EventId id = null;
	private Class<?> type;
	private Scope scope;
	private String method;
	private Object data;
	private final MetaData metaData = new MetaData();

	/**
	 * The causes of one event as a set of causing IDs.
	 */
	private Set<EventId> causes = new HashSet<EventId>();

	// TODO move event creation into factory?

	/**
	 * @param type
	 * @param data
	 */
	public Event(final Scope scope, final Class<?> type, final Object data) {
		this.scope = scope;
		this.type = type;
		this.data = data;
	}

	public Event(final Class<?> type) {
		this.type = type;
	}

	/**
	 * Construct empty event. Only metadata is initialized.
	 */
	public Event() {
	}

	/**
	 * @return the Java type of the payload
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type
	 *            the Java type to set for the Event payload
	 */
	public void setType(final Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
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
		return scope;
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
	public void setId(final ParticipantId senderId,
			final long sequenceNumber) {
		id = new EventId(senderId, sequenceNumber);
	}
	
	/**
	 * Sets the id of this event. Afterwards {@link #getId()} can return an id.
	 * 
	 * @param id new id to set
	 */
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
	public ParticipantId getSenderId() {
		return getId().getParticipantId();
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
	public long getSequenceNumber() {
		return getId().getSequenceNumber();
	}

	public EventId getId() {
		if (id == null) {
			throw new IllegalStateException(
					"The id of this event is not defined yet, "
							+ "because it was not sent by an informer so far.");
		}
		return id;
	}

	/**
	 * Returns a {@link MetaData} instance representing the meta data for this
	 * event.
	 * 
	 * @return meta data of this event, not <code>null</code>
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	public String toString() {
		return "Event[id=" + getId() + ", scope=" + scope + ", type =" + type
				+ ", metaData=" + metaData + ", causes = " + causes.toString()
				+ "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaData == null) ? 0 : metaData.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + causes.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Event other = (Event) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (metaData == null) {
			if (other.metaData != null) {
				return false;
			}
		} else if (!metaData.equals(other.metaData)) {
			return false;
		}
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equalsIgnoreCase(other.method)) {
			return false;
		}
		if (scope == null) {
			if (other.scope != null) {
				return false;
			}
		} else if (!scope.equals(other.scope)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (!causes.equals(other.causes)) {
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
	public boolean addCause(final EventId id) {
		return causes.add(id);
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
	public boolean removeCause(final EventId id) {
		return causes.remove(id);
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
	public boolean isCause(final EventId id) {
		return causes.contains(id);
	}

	/**
	 * Returns all causing events marked so far.
	 * 
	 * @return set of causing event ids. Modifications to this set do not affect
	 *         this event as it is a copy.
	 */
	public Set<EventId> getCauses() {
		return new HashSet<EventId>(causes);
	}

}
