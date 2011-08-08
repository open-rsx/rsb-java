package rsb.patterns;

import java.util.logging.Logger;

import rsb.AbstractEventHandler;
import rsb.Event;
import rsb.RSBException;

/**
 * A wrapper class to encapsulate AbstractDataHandler instances
 * which are used at the user-level to implement method handlers
 * as regular data handlers. This is needed for the handling of
 * request events in the method server.
 * 
 * @author swrede
 *
 * @param <T>	return type of invoke method
 * @param <U>	parameter object type of invoke method
 */
public class RequestHandler<T, U> extends AbstractEventHandler {

	private final static Logger LOG = Logger.getLogger(RequestHandler.class.getName());
	
	LocalMethod method;
	DataCallback<T, U> callback;
	
	public RequestHandler(LocalMethod method, DataCallback<T, U> callback) {
		this.method = method;
		this.callback = callback;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(Event event) {
		T result = null;
		String error = null;
		
		// invoke callback
		try {
			result = callback.invoke((U) event.getData());
		} catch (Throwable e) {
			LOG.warning("Exception during method invocation in participant: " + method.REQUEST_SCOPE + " Exception message: " + e);
			error = e.toString() + " Details: " + e.getMessage();
		}

		// return reply as direct event, hence set some metadata here
		Event reply = new Event();
		reply.setMethod("REPLY");
		reply.getMetaData().setUserInfo("rsb:reply",event.getId().getAsUUID().toString());
		reply.setScope(method.REPLY_SCOPE);

		if (error == null) {
			// return result of invocation
			reply.setType(result.getClass());
			reply.setData(result);
		} else {
			// return error information
			reply.setType(String.class);
			reply.getMetaData().setUserInfo("isException", "true");
			reply.setData(error);
		}
		
		// send reply via method informer
		try {
			method.getInformer().send(reply);
		} catch (RSBException e) {
			// TODO call local error handler
			LOG.severe("Exception while sending reply in server method: " + method.REPLY_SCOPE + " Exception message: " + e.getMessage());
		}		
	}
	
}