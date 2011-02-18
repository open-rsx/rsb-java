/**
 * 
 */
package rsb.transport.spread;

import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class DataMessage {
	
	private final static Logger log = Logger.getLogger(DataMessage.class.getName());	
	
	// from spread.SpreadConnection 
	private static final int SPREAD_OVERHEAD_OFFSET = 20000; 
	public static final int MAX_MESSAGE_LENGTH = 140000 - SPREAD_OVERHEAD_OFFSET;
	
	// shall a message also be delivered to the sender
	protected boolean selfDiscard = false;
	
	String[] groups;
	byte[] payload;
	
	public DataMessage(byte[] d) {
		payload = d;
	}

	public DataMessage(byte[] d, String[] grps) {
		groups = grps; 
		payload = d;
	}
	
	public String[] getGroups() {
		return groups;
	}
	
	public void setGroups(String[] grp) {
		groups = grp;
	}
	
	public boolean inGroup(String name) {
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].equals(name)) {
				return true;
			} 			
		}
		return false;
	}
	
	public void setData(byte[] d) {
		payload = d;
	}
	
	public byte[] getData() {
		return payload;
	}
	
	public void enableSelfDiscard() {
		selfDiscard = true;
	}
	
}
