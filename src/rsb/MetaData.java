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

	public Id getSenderId() {
		return senderId;
	}

	public void setSenderId(Id senderId) {
		this.senderId = senderId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long time) {
		if (time == 0) {
			createTime = System.nanoTime() / 1000;
		} else {
			createTime = time;
		}
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long time) {
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long time) {
	}

	public long getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(long time) {
	}

	public Set<String> userTimeKeys() {
	}

	public boolean hasUserTime(String key) {
	}

	public long getUserTime(String key) {
	}

	public void setUserTime(String key, long time) {
	}

	public Set<String> userInfoKeys() {
	}

	public boolean hasUserInfo(String key) {
	}

	public String getUserInfo(String key) {
	}

	public void setUserInfo(String key, String value) {
	}

}
