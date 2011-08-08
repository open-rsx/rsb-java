package rsb.patterns;

import rsb.AbstractDataHandler;
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
public class RequestHandler<T, U> extends AbstractDataHandler<U> {

	LocalMethod method;
	DataCallback<T, U> callback;
	
	public RequestHandler(LocalMethod method, DataCallback<T, U> callback) {
		this.method = method;
	}
	
	@Override
	public void handleEvent(U data) {
		// TODO Convert data
		// and dispatch to client
		// TODO check if we do not need an event handler here
		// due to the necessary metadata
		T result = null;
		try {
			result = callback.invoke(data);
		} catch (Throwable e) {
			// TODO Check how we deal with exceptions
			e.printStackTrace();
		}
		// Is the informer already ready to send any event
		// if configured with object?
		if (result != null) {
			Event reply = new Event(result.getClass());
			// reply.setMethod("REPLY");
			reply.setData(result);
			try {
				method.getInformer().send(reply);
			} catch (RSBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// method.getInformer().send(ReplyEvent);
		} else {
			// do something!
			// throw UserException("null result");
		}
	}
	
}