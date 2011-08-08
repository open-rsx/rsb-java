package rsb.patterns;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import rsb.Event;
import rsb.Handler;
import rsb.RSBException;
import rsb.filter.MethodFilter;

/**
 * Objects of this class represent methods provided by a remote
 * server.
 *
 * @author jmoringe
 * @author swrede
 *
 */
public class RemoteMethod<U, T> extends Method implements Handler {
	
	private final Map<UUID, AsyncRequest<U>> pendingRequests = new ConcurrentHashMap<UUID, AsyncRequest<U>>();
	
	/**
	 * Create a new RemoteMethod object that represent the remote method named @a
	 * name provided by @a server.
	 * 
	 * @param server
	 *            The remote server providing the method.
	 * @param name
	 *            The name of the method.
	 */
	public RemoteMethod(final Server server, final String name) {
		super(server, name);
		listener = factory.createListener(REPLY_SCOPE);
		informer = factory.createInformer(REQUEST_SCOPE);		
		listener.addFilter(new MethodFilter("REPLY"));
		listener.addHandler(this, false);
	}

	// Blocking call
	public U call(final T data) throws RSBException {
		// build event and send it over the informer as request
		// return reply as direct event, hence set some metadata here
		final Event request = new Event(data.getClass());
		request.setScope(REQUEST_SCOPE);		
		request.setMethod("REQUEST");
		request.setData(data);

		// instantiate future
		final AsyncRequest<U> future = new AsyncRequest<U>();
		Event sentEvent = null;
		synchronized (this) {
			sentEvent = informer.send(request);
			// put future with id in pending results table
			pendingRequests.put(sentEvent.getId().getAsUUID(), future);
		}
		
		// wait on result in get or return exception
		U result = null;
		try {
			result = future.get();
		} catch (InterruptedException exception) {
			LOG.warning("InterruptedException during wait for server reply. Re-throwing exception.");
			throw new RSBException(exception);
		} catch (ExecutionException exception) {
			LOG.warning("ExceutionException during remote method call. Re-throwing exception.");
			throw new RSBException(exception);
		} finally {
			// remove request from pending calls
			pendingRequests.remove(sentEvent);
		}
		return result;
	}
	
//	// Async call, returns future
//	public U callAsync(T parameter) {
//		
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void internalNotify(final Event event) {
		UUID replyId = null;
		// get reply id
		try {
			final String replyKey = event.getMetaData().getUserInfo("rsb:reply");
			replyId = UUID.fromString(replyKey);
		} catch (IllegalArgumentException exception) {
			LOG.warning("Received reply event without rsb:reply userinfo item. Skipping it.");
			return;
		}
		
		// check for reply id in list of pending calls
		if (pendingRequests.containsKey(replyId)) {
			final AsyncRequest<U> request = pendingRequests.get(replyId);
			// error case
			try {
				if (event.getMetaData().getUserInfo("isException")!=null) {
					final String error = (String) event.getData();
					request.error(new RSBException(error));
					return;
				}
			} catch (IllegalArgumentException exception) {
				// ignore
			}
			// regular case
			request.complete((U) event.getData());
		}	
	}
};