/**
 * 
 */
package rsb.transport.spread;

import java.util.logging.Logger;

import rsb.RSBEvent;
import spread.SpreadGroup;
import spread.SpreadMessage;

/**
 * @author swrede
 *
 */
public class SpreadMessageBuilder {	
	    
    private final static Logger log = Logger.getLogger(SpreadMessageBuilder.class.getName());
	String group;
	
	public SpreadMessageBuilder(String group) {
            log.info("SpreadMessageBuilder constructor");
            this.group = group;
	}

	public RSBEvent convert(DataMessage msg) {
		// TODO parse Notification
		return new RSBEvent();
	}
		
//	public DataMessage convert(RSBEvent e) {
//		// TODO serialize user types
//		String[] groups = new String[1];
//		groups[0] = group;
//		DataMessage m = new DataMessage(e.getData(),groups);
//		return m;
//	}
	
	static SpreadMessage convertDataMessage(DataMessage dmsg) throws SerializeException {
		SpreadMessage msg = new SpreadMessage();
		if (dmsg.getGroups().length==0) {
			throw new SerializeException("Receiver information is missing in DataMessage");
		}
		msg.addGroups(dmsg.getGroups());
		byte[] buf = null;
		if (dmsg.getData()!=null) {
			buf = dmsg.getData();
		}
		if ((buf.length==0) || (buf.length>DataMessage.MAX_MESSAGE_LENGTH)) {
			throw new SerializeException("Invalid Length of SpreadMessage (either null or larger than " + DataMessage.MAX_MESSAGE_LENGTH + " bytes)");
		}
		msg.setData(buf);
		if (dmsg.selfDiscard) {
			msg.setSelfDiscard(true);
		}
		// TODO marshal binary content
		return msg;
	}
	
	static DataMessage convertSpreadMessage(SpreadMessage msg) {
		if (!msg.isMembership()) {
			DataMessage data = null;
			data = new DataMessage(msg.getData());
			SpreadGroup[] sg = msg.getGroups();
			String[] grps = new String[sg.length];
			for (int i = 0; i < grps.length; i++) {
				grps[i] = sg[i].toString();
			}
			data.setGroups(grps);
			// TODO unmarshal binary content
			return data;
		} else {
			return null;
		}
	}
	
}
