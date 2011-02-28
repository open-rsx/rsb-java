/**
 * 
 */
package rsb.filter;

import java.util.logging.Logger;

import rsb.RSBEvent;
import rsb.event.EventId;

/**
 * @author swrede
 *
 */
public class ScopeFilter extends AbstractFilter {

	protected static Logger log = Logger.getLogger(ScopeFilter.class.getName());

	// TODO Add Scope Support once we defined the URI scheme

	String uri;
	
	public ScopeFilter(String uri) {
		super("ScopeFilter");
		this.uri = uri;
	}
	
	/*
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}	

	public void setUri(String u) {
		this.uri = u;		
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public void skip(EventId id) {
		log.info("Event with ID " + id + " will not be matched by ScopeFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Filter that) {
		return that instanceof ScopeFilter
		&& uri.equals(((ScopeFilter) that).getUri());
	}

	@Override
	public RSBEvent transform(RSBEvent e) {
		// TODO implement real scoping, currently it is solely an equality test on the publisher's URI
		//      as subscription URI in the ScopeFilter and PublisherURI must match. 
    	log.info("ScopeFilter with scope " + uri + " received event to transform.");
    	if(e.getUri()!=null)
    		log.fine("  Event's receiver URI = " + e.getUri());
    	boolean matches = false;
    	String evtId = e.getId().toString();
    	if(mustSkip(evtId)) {
    		log.fine("event with ID " + evtId + " whitelisted in ScopeFilter!");
    		matches = true;
    		skipped(evtId);
    	} else {
    		//matches = (scope.isParentScopeOf(e.getMetadata().getReceiverUri()));
    		matches = uri.equals(e.getUri());
    	}
    	if (matches) {
    		log.info("ScopeFilter matched successfully!");
    	} else {
    		log.info("ScopeFilter rejected event!");
    	}
   		return matches ? e : null;
	}


}
