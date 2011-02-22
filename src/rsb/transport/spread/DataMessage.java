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
package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import spread.SpreadMessage;

/**
 * @author swrede
 *
 */
public class DataMessage {
	
	// TODO Support larger message frames, currently the assumption is that messages are not longer than MAX_MESSAGE_LENGTH
	// TODO add GroupName length checks
	
	private final static Logger log = Logger.getLogger(DataMessage.class.getName());	
	
	/* from spread.SpreadConnection */ 
	private static final int SPREAD_OVERHEAD_OFFSET = 20000; 
	public static final int MAX_MESSAGE_LENGTH = 140000 - SPREAD_OVERHEAD_OFFSET;
	
	/* decorated spread message */ 
	SpreadMessage msg = new SpreadMessage();

	public DataMessage() {
		
	}
	
	static DataMessage convertSpreadMessage(SpreadMessage msg) throws SerializeException {
		if (!msg.isMembership()) {
			DataMessage dm = new DataMessage();
			dm.msg = msg;
			return dm;
		} else {
			throw new SerializeException("MembershipMessage received but DataMessage expected!");
		}
	}	
	
	private void checkSize(ByteBuffer b) throws SerializeException {
		if ((b.limit()==0) || (b.limit()>DataMessage.MAX_MESSAGE_LENGTH)) {
			throw new SerializeException("Invalid Length of SpreadMessage (either null or larger than " + DataMessage.MAX_MESSAGE_LENGTH + " bytes)");
		}		
	}

	public String[] getGroups() {
		String[] groups = new String[msg.getGroups().length];
		for (int i = 0; i < msg.getGroups().length; i++) {
			groups[i] = msg.getGroups()[i].toString();
		}
		return groups;
	}
	
	public void setGroups(String[] grp) {
		for (int i = 0; i < grp.length; i++) {
			msg.addGroup(grp[i]);
		}		
	}
	
	public void addGroup(String grp) {
		msg.addGroup(grp);
	}
	
	public boolean inGroup(String name) {
		for (int i = 0; i < msg.getGroups().length; i++) {
			if (msg.getGroups()[i].equals(name)) {
				return true;
			} 			
		}
		return false;
	}
	
	public void setData(ByteBuffer b) throws SerializeException {
		checkSize(b);
		msg.setData(b.array());
	}

	public void setData(byte[] b) throws SerializeException {
		msg.setData(b);
	}	
	
	public ByteBuffer getData() {
		return ByteBuffer.wrap(msg.getData());
	}
	
	public void enableSelfDiscard() {
		msg.setSelfDiscard(true);
	}
	
	public SpreadMessage getSpreadMessage() throws SerializeException {
		if (msg.getGroups().length==0) {
			throw new SerializeException("Receiver information is missing in DataMessage");
		}	
		return msg;
	}
	
}
