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

/**
 * Basic event structure exchanged between RSB ports. It is a combination of
 * metadata and the actual data to publish / subscribe to as payload.
 *
 * @author swrede
 */
// TODO check if we want to provide the type via a template parameter
// Rationale: A single event can only hold a single type
public class Event {

	private Id id = null;
	private Class<?> type;
	private Scope scope;
    private long sequenceNumber;
	private Object data;
	private MetaData metaData = new MetaData();
	private ParticipantId senderId = null;

	// TODO move event creation into factory?

	/**
	 * @param type
	 * @param data
	 */
	public Event(Scope scope, Class<?> type, Object data) {
		this.scope = scope;
		this.type = type;
		this.data = data;
	}


	public Event(Class<?> type) {
		this.type = type;
	}

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
	public void setType(Class<?> type) {
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
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() {
		return scope;
	}
	

	public void setSenderId(ParticipantId id) {
		this.senderId  = id;
	}	
	
	public ParticipantId getSenderId() {
		return senderId;
	}
	
	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

    public long getSequenceNumber() {
	        return sequenceNumber;
	}

    public void setSequenceNumber(long sequenceNumber) {
	        this.sequenceNumber = sequenceNumber;
	}

	public Id getId() {
	        if (id == null) {
		        id = new Id(senderId, sequenceNumber);
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

	public String toString() {
	        return "Event[id=" + getId() + ", scope=" + scope
		    + ", seqnum=" + sequenceNumber + ", type =" + type
		    + ", metaData=" + metaData + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaData == null) ? 0 : metaData.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result
				+ ((senderId == null) ? 0 : senderId.hashCode());
		result = prime * result
				+ (int) (sequenceNumber ^ (sequenceNumber >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Event other = (Event) obj;
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
		if (scope == null) {
			if (other.scope != null) {
				return false;
			}
		} else if (!scope.equals(other.scope)) {
			return false;
		}
		if (senderId == null) {
			if (other.senderId != null) {
				return false;
			}
		} else if (!senderId.equals(other.senderId)) {
			return false;
		}
		if (sequenceNumber != other.sequenceNumber) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}



}
