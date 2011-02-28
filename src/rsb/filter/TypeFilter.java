/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.RSBEvent;
import rsb.event.EventId;

/**
 *
 * @author swrede
 */
public class TypeFilter extends AbstractFilter {
    
    @SuppressWarnings("unchecked")
	Class type;
    
    public TypeFilter () {
        super("TypeFilter");
    }
    
    @SuppressWarnings("unchecked")
	public TypeFilter(Class c) {
    	super("TypeFilter");
        type = c;
    }
	
	public void skip(EventId id) {
		log.info("Event with ID " + id + " will not be matched by TypeFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Filter that) {
		return that instanceof TypeFilter
	       && type.equals(((TypeFilter) that).type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RSBEvent transform(RSBEvent e) {
    	// check skip
    	String evtId = e.getId().toString();
    	if(mustSkip(evtId)) {
    		log.info("event with ID " + evtId + " whitelisted in TypeFilter!");
    		skipped(evtId);
    		return e;
    	}
        // condition: class types are equal
        if (type.isAssignableFrom(e.getClass())) {
            return e;
        } else {
            return null;
        }		
	}
    
}
