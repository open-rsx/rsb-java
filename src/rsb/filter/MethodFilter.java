package rsb.filter;

import rsb.Event;

public class MethodFilter extends AbstractFilter {
	
	//private final static Logger LOG = Logger.getLogger(MethodFilter.class.getName());
	
	String method;
	
	public MethodFilter(String method) {
		super(MethodFilter.class);
		this.method = method;
	}

	@Override
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
		
	}

	@Override
	public Event transform(Event e) {
		Event result = null;
		if (e.getMethod()!=null && e.getMethod().equalsIgnoreCase(method)) {
			
			result = e;
		}
		return result;
	}
}