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

import rsb.util.ExactTime;

/**
 * Framework-supplied meta data attached to each event that give information
 * e.g. about timing issues.
 *
 * New instances have creation time set to the time of creation of the meta data
 * instance.
 *
 * In case you are setting timestamps manually, ensure to create these
 * timestamps using {@link ExactTime} to get the highest possible resolution.
 *
 * @author jwienke
 */
public class MetaData {

    private long createTime = ExactTime.currentTimeMicros();
    private long sendTime = 0;
    private long receiveTime = 0;
    private long deliverTime = 0;
    private final Map<String, Long> userTimes = new HashMap<String, Long>();
    private final Map<String, String> userInfos = new HashMap<String, String>();

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
        return this.createTime;
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
    public void setCreateTime(final long time) {
        if (time <= 0) {
            this.createTime = ExactTime.currentTimeMicros();
        } else {
            this.createTime = time;
        }
    }

    /**
     * Returns the time at which the generated notification for an event was
     * sent on the bus (after serialization).
     *
     * @return timestamp in microseconds
     */
    public long getSendTime() {
        return this.sendTime;
    }

    /**
     * Sets the time at which the generated notification for an event was sent
     * on the bus (after serialization).
     *
     * @param time
     *            timestamp in microseconds or 0 to use current system time
     */
    public void setSendTime(final long time) {
        if (time <= 0) {
            this.sendTime = ExactTime.currentTimeMicros();
        } else {
            this.sendTime = time;
        }
    }

    /**
     * Returns the time at which an event is received by listener in its encoded
     * form.
     *
     * @return timestamp in microseconds
     */
    public long getReceiveTime() {
        return this.receiveTime;
    }

    /**
     * Sets the time at which an event is received by listener in its encoded
     * form.
     *
     * @param time
     *            timestamp in microseconds or 0 to use current system time
     */
    public void setReceiveTime(final long time) {
        if (time <= 0) {
            this.receiveTime = ExactTime.currentTimeMicros();
        } else {
            this.receiveTime = time;
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
        return this.deliverTime;
    }

    /**
     * Sets the time at which an event was decoded and will be dispatched to the
     * client as soon as possible (set directly before passing it to the client
     * handler).
     *
     * @param time
     *            timestamp in microseconds or 0 to use current system time
     */
    public void setDeliverTime(final long time) {
        if (time <= 0) {
            this.deliverTime = ExactTime.currentTimeMicros();
        } else {
            this.deliverTime = time;
        }
    }

    /**
     * Returns the keys of all available user times.
     *
     * @return set of all keys
     */
    public Set<String> userTimeKeys() {
        return this.userTimes.keySet();
    }

    /**
     * Checks whether a user-provided timestamp with the given key exists.
     *
     * @param key
     *            the key to check
     * @return <code>true</code> if a timestamp for the given key exists, else
     *         <code>false</code>
     */
    public boolean hasUserTime(final String key) {
        return this.userTimes.containsKey(key);
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
    public long getUserTime(final String key) {
        if (!this.userTimes.containsKey(key)) {
            throw new IllegalArgumentException("No user time with key " + key);
        }
        return this.userTimes.get(key);
    }

    /**
     * Sets a user timestamp and replaces existing entries.
     *
     * @param key
     *            the key for the timestamp
     * @param time
     *            time in microseconds or 0 to use current system time
     */
    public void setUserTime(final String key, final long time) {
        if (time <= 0) {
            this.userTimes.put(key, ExactTime.currentTimeMicros());
        } else {
            this.userTimes.put(key, time);
        }
    }

    /**
     * Returns all keys of user-defined infos.
     *
     * @return set of all defined keys
     */
    public Set<String> userInfoKeys() {
        return this.userInfos.keySet();
    }

    /**
     * Checks whether a user info exists under the provided key.
     *
     * @param key
     *            key to check
     * @return <code>true</code> if an info for the key is defined, else
     *         <code>false</code>
     */
    public boolean hasUserInfo(final String key) {
        return this.userInfos.containsKey(key);
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
    public String getUserInfo(final String key) {
        if (!this.userInfos.containsKey(key)) {
            throw new IllegalArgumentException(
                    "No user info available for key " + key);
        }
        return this.userInfos.get(key);
    }

    /**
     * Sets a user info with the specified key and value or replaces and already
     * existing one.
     *
     * @param key
     *            the key to set
     * @param value
     *            the user value
     */
    public void setUserInfo(final String key, final String value) {
        this.userInfos.put(key, value);
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionals - Simplify code in this case
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (int) (this.createTime ^ (this.createTime >>> 32));
        result = prime * result
                + (int) (this.deliverTime ^ (this.deliverTime >>> 32));
        result = prime * result
                + (int) (this.receiveTime ^ (this.receiveTime >>> 32));
        result = prime * result
                + (int) (this.sendTime ^ (this.sendTime >>> 32));
        result = prime * result
                + ((this.userInfos == null) ? 0 : this.userInfos.hashCode());
        result = prime * result
                + ((this.userTimes == null) ? 0 : this.userTimes.hashCode());
        return result;
    }
    // CHECKSTYLE.ON: AvoidInlineConditionals

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MetaData)) {
            return false;
        }
        final MetaData other = (MetaData) obj;
        if (this.createTime != other.createTime) {
            return false;
        }
        if (this.deliverTime != other.deliverTime) {
            return false;
        }
        if (this.receiveTime != other.receiveTime) {
            return false;
        }
        if (this.sendTime != other.sendTime) {
            return false;
        }
        if (this.userInfos == null) {
            if (other.userInfos != null) {
                return false;
            }
        } else if (!this.userInfos.equals(other.userInfos)) {
            return false;
        }
        if (this.userTimes == null) {
            if (other.userTimes != null) {
                return false;
            }
        } else if (!this.userTimes.equals(other.userTimes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MetaData[createTime = " + this.createTime + ", sendTime = "
                + this.sendTime + ", receiveTime = " + this.receiveTime
                + ", userTimes = " + this.userTimes + ", userInfos = "
                + this.userInfos + "]";
    }
}
