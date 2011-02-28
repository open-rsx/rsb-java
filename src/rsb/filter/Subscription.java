/**
 * 
 */
package rsb.filter;

import java.util.ArrayList;
import java.util.Iterator;

import rsb.RSBEvent;
import rsb.event.RSBEventListener;


/**
 * @author swrede
 *
 */
public class Subscription {

	ArrayList<Filter> filters = new ArrayList<Filter>();
	@SuppressWarnings("unchecked")
	ArrayList<RSBEventListener> handler = new ArrayList<RSBEventListener>();

    @SuppressWarnings("unchecked")
	public Subscription appendHandler(RSBEventListener l) {
    	handler.add(l);
    	return this;
	}
    
    public Subscription appendFilter(Filter f) {
    	filters.add(f);
    	return this;
	}    
    
    @SuppressWarnings("unchecked")
	public Iterator<RSBEventListener> getHandlerIterator() {
    	Iterator<RSBEventListener> it = handler.iterator();
		return it;
	}

    public Iterator<Filter> getFilterIterator() {
    	Iterator<Filter> it = filters.iterator();
		return it;
	}    
    
	public int length() {
		return filters.size();
	}	
	
	public boolean match(RSBEvent e) {
		for (Filter f: filters) {
			e = f.transform(e);
			if (e==null) return false;			
		}
		return true;
	}
	
}
