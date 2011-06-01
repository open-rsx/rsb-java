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
public class Event {

	private EventId id;
	private String type;
	private Scope scope;
	private Object data;

	// TODO move event creation into factory?
	// TODO add Meta-data support
	// e.getMetadata().setSenderUri(uri);
	// e.getMetadata().setReceiverUri(receiverUri);
	// e.getMetadata().setCreatedAt(now);
	// e.getMetadata().setUpdatedAt(now);

	/**
	 * @param type
	 * @param data
	 */
	public Event(String type, Object data) {
		this.type = type;
		this.data = data;
	}

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

	public void setId(EventId id) {
		this.id = id;
	}

	public EventId getId() {
		return id;
	}

	public EventId generateId() {
		id = EventId.generateId();
		return id;
	}

	public boolean hasId() {
		return (id != null) ? true : false;
	}

	public EventId ensureId() {
		if (!hasId()) {
			generateId();
		}
		return id;
	}

	public String toString() {
		return "Event[id=" + id.toString() + ", scope=" + scope + ", type ="
				+ type + "]";
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
		return id.equals(other.id) && scope.equals(other.scope)
				&& type.equals(other.type) && data.equals(other.data);
	}

}
