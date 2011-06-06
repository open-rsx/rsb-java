/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software{} you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation{}
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY{} without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */

package rsb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Framework-supplied meta data attached to each event that give information
 * e.g. about timing issues.
 * 
 * @author jwienke
 */
public class MetaData {

	private Id senderId = null;
	private long createTime = System.nanoTime() / 1000;
	private long sendTime = 0;
	private long receiveTime = 0;
	private long deliverTime = 0;
	private Map<String, Long> userTimes = new HashMap<String, Long>();
	private Map<String, String> userInfos = new HashMap<String, String>();

	/**
	 * Creates a new {@link MetaData} instance with unspecified sender ID and
	 * creation time now.
	 */
	public MetaData() {
	}

	/**
	 * Creates a new {@link MetaData} instance with the specified sender ID and
	 * creation time now.
	 * 
	 * @param senderId
	 *            the id of the sender, not <code>null</code>
	 */
	public MetaData(Id senderId) {
		assert senderId != null;
		this.senderId = senderId;
	}

	/**
	 * Returns the ID (a UUID) of the sending participant.
	 * 
	 * @return UUID, may be <code>null</code> if not specified
	 */
	public Id getSenderId() {
		return senderId;
	}

	/**
	 * Sets the ID (a UUID) of the sending participant.
	 * 
	 * @param senderId
	 *            id of the sending participant
	 */
	public void setSenderId(Id senderId) {
		this.senderId = senderId;
	}

	/**
	 * Returns a time stamp that is automatically filled with the time the event
	 * instance was created by the language binding. This should usually reflect
	 * the time at which the notified condition most likely occurred in the
	 * sender. If event instances are reused, it has to be reset manually by the
	 * client.
	 * 
	 * This timestamp is initially set to the creating time stamp of this
	 * instance.
	 * 
	 * @return timestamp in microseconds
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the time stamp that is automatically filled with the time the event
	 * instance was created by the language binding. This should usually reflect
	 * the time at which the notified condition most likely occurred in the
	 * sender. If event instances are reused, it has to be reset manually by the
	 * client.
	 * 
	 * @param time
	 *            timestamp in microseconds or 0 to use current system time
	 */
	public void setCreateTime(long time) {
		if (time <= 0) {
			createTime = System.nanoTime() / 1000;
		} else {
			createTime = time;
		}
	}

	/**
	 * Returns the time at which the generated notification for an event was
	 * sent on the bus (after serialization).
	 * 
	 * @return timestamp in microseconds
	 */
	public long getSendTime() {
		return sendTime;
	}

	/**
	 * Sets the time at which the generated notification for an event was sent
	 * on the bus (after serialization).
	 * 
	 * @param time
	 *            timestamp in microseconds or 0 to use current system time
	 */
	public void setSendTime(long time) {
		if (time <= 0) {
			sendTime = System.nanoTime() / 1000;
		} else {
			sendTime = time;
		}
	}

	/**
	 * Returns the time at which an event is received by listener in its encoded
	 * form.
	 * 
	 * @return timestamp in microseconds
	 */
	public long getReceiveTime() {
		return receiveTime;
	}

	/**
	 * Sets the time at which an event is received by listener in its encoded
	 * form.
	 * 
	 * @param time
	 *            timestamp in microseconds or 0 to use current system time
	 */
	public void setReceiveTime(long time) {
		if (time <= 0) {
			receiveTime = System.nanoTime() / 1000;
		} else {
			receiveTime = time;
		}
	}

	/**
	 * Returns the time at which an event was decoded and will be dispatched to
	 * the client as soon as possible (set directly before passing it to the
	 * client handler).
	 * 
	 * @return timestamp in microseconds
	 */
	public long getDeliverTime() {
		return deliverTime;
	}

	/**
	 * Sets the time at which an event was decoded and will be dispatched to the
	 * client as soon as possible (set directly before passing it to the client
	 * handler).
	 * 
	 * @param time
	 *            timestamp in microseconds or 0 to use current system time
	 */
	public void setDeliverTime(long time) {
		if (time <= 0) {
			deliverTime = System.nanoTime() / 1000;
		} else {
			deliverTime = time;
		}
	}

	/**
	 * Returns the keys of all available user times.
	 * 
	 * @return set of all keys
	 */
	public Set<String> userTimeKeys() {
		return userTimes.keySet();
	}

	/**
	 * Checks whether a user-provided timestamp with the given key exists
	 * 
	 * @param key
	 *            the key to check
	 * @return <code>true</code> if a timestamp for the given key exists, else
	 *         <code>false</code>
	 */
	public boolean hasUserTime(String key) {
		return userTimes.containsKey(key);
	}

	/**
	 * Returns the user timestamp stored under the provided key.
	 * 
	 * @param key
	 *            key of the user-provided timestamp
	 * @return timetamp
	 * @throw IllegalArgumentException no timestamp stored und the provided key
	 */
	public long getUserTime(String key) {
		if (!userTimes.containsKey(key)) {
			throw new IllegalArgumentException("No user time with key " + key);
		}
		return userTimes.get(key);
	}

	/**
	 * Sets a user timestamp and replaces existing entries.
	 * 
	 * @param key
	 *            the key for the timestamp
	 * @param time
	 *            time in microseconds or 0 to use current system time
	 */
	public void setUserTime(String key, long time) {
		if (time <= 0) {
			userTimes.put(key, System.nanoTime() / 1000);
		} else {
			userTimes.put(key, time);
		}
	}

	/**
	 * Returns all keys of user-defined infos.
	 * 
	 * @return set of all defined keys
	 */
	public Set<String> userInfoKeys() {
		return userInfos.keySet();
	}

	/**
	 * Checks whether a user info exists under the provided key.
	 * 
	 * @param key
	 *            key to check
	 * @return <code>true</code> if an info for the key is defined, else
	 *         <code>false</code>
	 */
	public boolean hasUserInfo(String key) {
		return userInfos.containsKey(key);
	}

	/**
	 * Returns the user-defined string for the given key.
	 * 
	 * @param key
	 *            key to look up
	 * @return user info given for this key
	 * @throw IllegalArgumentException no info set for the specified key
	 */
	public String getUserInfo(String key) {
		if (!userInfos.containsKey(key)) {
			throw new IllegalArgumentException(
					"No user info available for key " + key);
		}
		return userInfos.get(key);
	}

	/**
	 * Sets a user info with the specified key and value or replaces and already
	 * existing one
	 * 
	 * @param key
	 *            the key to set
	 * @param value
	 *            the user value
	 */
	public void setUserInfo(String key, String value) {
		userInfos.put(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MetaData)) {
			return false;
		}
		MetaData other = (MetaData) obj;
		if (senderId == null ^ other.senderId == null) {
			return false;
		}
		return (senderId == null || senderId.equals(other.senderId))
				&& (createTime == other.createTime)
				&& (sendTime == other.sendTime)
				&& (receiveTime == other.receiveTime)
				&& (deliverTime == other.deliverTime)
				&& (userInfos.equals(other.userInfos))
				&& (userTimes.equals(other.userTimes));
	}

	@Override
	public int hashCode() {
		return (int) (senderId.hashCode() + 3 * createTime - 7 * sendTime + 5
				* receiveTime - 13 * deliverTime + 29 * userTimes.hashCode() - userInfos
				.hashCode());
	}

	@Override
	public String toString() {
		return "MetaData[senderId = " + senderId + ", createTime = "
				+ createTime + ", sendTime = " + sendTime + ", receiveTime = "
				+ receiveTime + ", userTimes = " + userTimes + ", userInfos = "
				+ userInfos + "]";
	}
}
