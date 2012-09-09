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

	private long createTime = System.currentTimeMillis() * 1000;
	private long sendTime = 0;
	private long receiveTime = 0;
	private long deliverTime = 0;
	private Map<String, Long> userTimes = new HashMap<String, Long>();
	private Map<String, String> userInfos = new HashMap<String, String>();

	/**
	 * Creates a new {@link MetaData} instance with creation time now.
	 */
	public MetaData() {
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
			createTime = System.currentTimeMillis() * 1000;
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
			sendTime = System.currentTimeMillis() * 1000;
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
			receiveTime = System.currentTimeMillis() * 1000;
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
			deliverTime = System.currentTimeMillis() * 1000;
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
	 * @throws IllegalArgumentException
	 *             no timestamp stored und the provided key
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
			userTimes.put(key, System.currentTimeMillis() * 1000);
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
	 * @throws IllegalArgumentException
	 *             no info set for the specified key
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



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (createTime ^ (createTime >>> 32));
		result = prime * result + (int) (deliverTime ^ (deliverTime >>> 32));
		result = prime * result + (int) (receiveTime ^ (receiveTime >>> 32));
		result = prime * result + (int) (sendTime ^ (sendTime >>> 32));
		result = prime * result
				+ ((userInfos == null) ? 0 : userInfos.hashCode());
		result = prime * result
				+ ((userTimes == null) ? 0 : userTimes.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MetaData)) {
			return false;
		}
		MetaData other = (MetaData) obj;
		if (createTime != other.createTime) {
			return false;
		}
		if (deliverTime != other.deliverTime) {
			return false;
		}
		if (receiveTime != other.receiveTime) {
			return false;
		}
		if (sendTime != other.sendTime) {
			return false;
		}
		if (userInfos == null) {
			if (other.userInfos != null) {
				return false;
			}
		} else if (!userInfos.equals(other.userInfos)) {
			return false;
		}
		if (userTimes == null) {
			if (other.userTimes != null) {
				return false;
			}
		} else if (!userTimes.equals(other.userTimes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MetaData[createTime = " + createTime + ", sendTime = "
				+ sendTime + ", receiveTime = " + receiveTime
				+ ", userTimes = " + userTimes + ", userInfos = " + userInfos
				+ "]";
	}
}
