/**
 * 
 */
package rsb.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rsb.RSBEvent;
import rsb.filter.Filter;


/**
 * @author swrede
 *
 */
public class Subscription {

	ArrayList<Filter> filters = new ArrayList<Filter>();
	@SuppressWarnings("unchecked")
	ArrayList<RSBListener> handler = new ArrayList<RSBListener>();

    @SuppressWarnings("unchecked")
	public Subscription appendHandler(RSBListener l) {
    	handler.add(l);
    	return this;
	}
    
    public Subscription appendFilter(Filter f) {
    	filters.add(f);
    	return this;
	}    
    
    @SuppressWarnings("unchecked")
	public Iterator<RSBListener> getHandlerIterator() {
    	Iterator<RSBListener> it = handler.iterator();
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

	public List<Filter> getFilter() {
		return filters;
	}

	public void dispatch(RSBEvent e) {
		for (RSBListener<RSBEvent> l: handler) {
			l.internalNotify(e);
		}		
	}

	@SuppressWarnings("unchecked")
	public List<RSBListener> getHandlers() {
		return handler;
	}
	
}
