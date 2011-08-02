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

import java.lang.StringBuilder;
import java.util.Formatter;

/**
 * Basic event structure exchanged between RSB ports. It is a combination of
 * metadata and the actual data to publish / subscribe to as payload.
 *
 * @author swrede
 */
public class Event {

	private Id id = null;
	private String type;
	private Scope scope;
        private long sequenceNumber;
	private Object data;
	private MetaData metaData = new MetaData();

	// TODO move event creation into factory?

	/**
	 * @param type
	 * @param data
	 */
	public Event(Scope scope, String type, Object data) {
		this.scope = scope;
		this.type = type;
		this.data = data;
	}

	// TODO check if we want this or if a constructor argument describing the data is better
	public Event(String type) {
		this.type = type;
	}

	public Event() {
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
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
		        computeId();
	        }
		return id;
	}

        private void computeId() {
	        StringBuilder builder = new StringBuilder();
	        Formatter formatter = new Formatter(builder);
	        formatter.format("%08x", getSequenceNumber());
	        // id = makeV5UUID(metaData.getSenderId(), builder.toString());
	        id = metaData.getSenderId();
		// TODO just pretend for now
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
		return id.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Event)) {
			return false;
		}
		Event other = (Event) obj;
		if (scope == null ^ other.scope == null) {
			return false;
		}
		if (type == null ^ other.type == null) {
			return false;
		}
		if (data == null ^ other.data == null) {
			return false;
		}
		return (sequenceNumber == other.sequenceNumber)
				&& (scope == null || scope.equals(other.scope))
				&& (type == null || type.equals(other.type))
				&& (data == null || data.equals(other.data))
				&& metaData.equals(other.metaData);
	}

}
