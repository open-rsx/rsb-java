package rsb.patterns;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

class ReplyCallback implements DataCallback<String, String> {
	
	private final static Logger LOG = Logger.getLogger(ReplyCallback.class.getName());
	
	AtomicBoolean flag = new AtomicBoolean(false);

	@Override
	public String invoke(String request) throws Throwable {
		LOG.info("ReplyCallback invoked with Request value: " + request);
		flag.set(true);
		return request;
	}
	
	public boolean wasCalled() {
		return flag.get();
	}
}