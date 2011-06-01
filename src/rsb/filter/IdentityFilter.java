/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.Event;
import rsb.EventId;

/**
 *
 * @author swrede
 */

/* 
 * TODO add functions to spread port for identity matching
 * based on the spread identity groups
 */
public class IdentityFilter extends AbstractFilter {
    
	String uri;
	Type type;
	boolean invert = false;
	
	public enum Type {
		SENDER_IDENTITY,
		RECEIVER_IDENTITY,
	}
	
    public IdentityFilter (String uri, Type type, boolean invert) {
        super("IdentityFilter");
        this.uri = uri;
        this.type = type;
        this.invert = invert;
    }

    public IdentityFilter (String uri, Type type) {
    	this(uri, type, false);
    }
    
    public Type getType() {
    	return type;
    }
    
    public String getUri() {
    	return uri;
    }
    
    public boolean isInverted() {
    	return invert;
    }
    
    public void setInvert(boolean b) {
    	invert = b;
    }
   
	/*
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}	

	public void skip(EventId id) {
		log.info("Event with ID " + id + " will not be matched by IdentityFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Filter that) {
		return that instanceof IdentityFilter
	       && uri.equals(((IdentityFilter) that).uri)
	       && type == ((IdentityFilter) that).type;
	}

	@Override
	public Event transform(Event e) {
		// TODO add implementation
		// currently, this is not supported due to lack of metadata
		return e;
	}
//    	log.info("IdentityFilter received event to transform.");
//    	if(!e.getMetadata().getSenderUri().isNull())
//    		log.debug("  Event's sender URI = " + e.getMetadata().getSenderUri().getUri());
//    	if(!e.getMetadata().getReceiverUri().isNull())
//    		log.debug("  Event's receiver URI = " + e.getMetadata().getReceiverUri().getUri());
//    	boolean matches = false;
//    	String evtId = e.toString().getID();
//    	if(mustSkip(evtId)) {
//    		log.debug("event with ID " + evtId + " whitelisted in IdentityFilter!");
//    		matches = true;
//    		skipped(evtId);
//    	} else {
//	    	switch(type) {
//	    	case SENDER_IDENTITY:
//	    		log.debug("matching event's sender URI against filter's URI '" + uri.getUri() + "'");
//	    		matches = (uri.equals(e.getMetadata().getSenderUri()));
//	    		break;
//	    	case RECEIVER_IDENTITY:
//	    		log.debug("matching event's recevier URI against filter's URI '" + uri.getUri() + "'");
//	    		matches = (uri.equals(e.getMetadata().getReceiverUri()));
//	    		break;
//	    	}
//	    	matches = invert ? !matches : matches;
//    	}
//    	if (matches) {
//    		log.debug("IdentityFilter matched successfully!");
//    	}
//   		return matches ? e : null;
//	}
	
}
