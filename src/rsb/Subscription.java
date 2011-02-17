/**
 * 
 */
package rsb;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author swrede
 *
 */
public class Subscription {

	ArrayList<MTF> filters = new ArrayList<MTF>();
	
    public Subscription append(MTF f) {
    	filters.add(f);
    	return this;
	}
    
    public Iterator<MTF> getFilterIterator() {
    	Iterator<MTF> it = filters.iterator();
		return it;
	}

	public int length() {
		return filters.size();
	}	
	
}
