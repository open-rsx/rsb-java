package rsb.patterns;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

class ReplyDataCallback implements DataCallback<String, String> {
	
	private final static Logger LOG = Logger.getLogger(ReplyDataCallback.class.getName());
	
	AtomicBoolean flag = new AtomicBoolean(false);
	public AtomicInteger counter = new AtomicInteger();

	@Override
	public String invoke(String request) throws Throwable {
		LOG.fine("ReplyCallback invoked with Request value: " + request);
		if (flag.get()==false) {
			flag.set(true);
		}
		counter.incrementAndGet();
//		} else {
//			throw new Exception("test exception in request handler");
//		}
		return request;
	}
	
	public boolean wasCalled() {
		return flag.get();
	}
}