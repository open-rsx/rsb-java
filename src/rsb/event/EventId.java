/**
 * 
 */
package rsb.event;

import java.util.UUID;

/**
 * This class serves as a Uniform Resource Name to identify 
 * events in an RSB system. At present, the URN is based on 
 * an UUID that shall is unique for each event instance.      
 * 
 * @author swrede
 *
 */
public class EventId {

	UUID id;
	
	/**
	 * 
	 */
	public EventId() {
		id = UUID.randomUUID();
	}
	
	public EventId(UUID u) {
		this.id = u;
	}
	
	public EventId(String sid) {
		id = UUID.fromString(sid.substring(8));
	}
	
	public String toString() {
		return "rsb:eid:"+id.toString();
	}
	
	public static EventId generateId() {		
		return new EventId();
	}
	
	public UUID get() {
		return id;
	}

	public boolean equals(EventId other) {
		return id.compareTo(other.id)==0;
	}
}
