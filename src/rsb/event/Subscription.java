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
 */
public class Subscription {

	ArrayList<Filter> filters = new ArrayList<Filter>();
	@SuppressWarnings("rawtypes")
	ArrayList<RSBListener> handler = new ArrayList<RSBListener>();

	public Subscription appendHandler(
			@SuppressWarnings("rawtypes") RSBListener l) {
		handler.add(l);
		return this;
	}

	public Subscription appendFilter(Filter f) {
		filters.add(f);
		return this;
	}

	@SuppressWarnings("rawtypes")
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
		for (Filter f : filters) {
			e = f.transform(e);
			if (e == null)
				return false;
		}
		return true;
	}

	public List<Filter> getFilter() {
		return filters;
	}

	public void dispatch(RSBEvent e) {
		for (RSBListener<RSBEvent> l : handler) {
			try {
				l.internalNotify(e);
			} catch (Exception ex) {
				// TODO add logger, re-throw exception to user-specified
				// exception handler
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public List<RSBListener> getHandlers() {
		return handler;
	}

}
