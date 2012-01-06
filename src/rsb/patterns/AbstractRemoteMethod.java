/**
 * ============================================================
 *
 * This file is part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.patterns;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import rsb.EventId;
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
 * @param <T>	return type
 * @param <U>	parameter object type
 *
 */
public abstract class AbstractRemoteMethod<T, U> extends Method implements Handler {

	private static final Logger LOG = Logger.getLogger(AbstractRemoteMethod.class.getName());

	protected class CollectorThread implements Runnable {
		public void run() {
			do {
				for (EventId key : pendingRequests.keySet()) {
					if (pendingRequests.get(key).isEnqueued()) {
						// future already cleared, thus we remove the pending request entry
						LOG.info("Removing request with id: " + key);
						pendingRequests.remove(key);
					}
				}
			} while (isActive());
		}
	}

	// assuming usually eight threads will write simultaneously to the map
	private final Map<EventId, WeakReference<Future<T>>> pendingRequests = new ConcurrentHashMap<EventId, WeakReference<Future<T>>>(16,0.75f,8);

	/**
	 * Create a new RemoteMethod object that represent the remote method named @a
	 * name provided by @a server.
	 *
	 * @param server
	 *            The remote server providing the method.
	 * @param name
	 *            The name of the method.
	 */
	public AbstractRemoteMethod(final Server server, final String name) {
		super(server, name);
		listener = factory.createListener(REPLY_SCOPE);
		informer = factory.createInformer(REQUEST_SCOPE);
		listener.addFilter(new MethodFilter("REPLY"));
		listener.addHandler(this, true);
	}

	public abstract Future<T> call(final U data) throws RSBException;

	/**
	 * @param event
	 * @return
	 * @throws RSBException
	 */
	protected Future<T> sendRequest(final Event event) throws RSBException {
		// set metadata
		event.setScope(REQUEST_SCOPE);
		event.setMethod("REQUEST");
		// further metadata is set by informer

		// instantiate future
		final Future<T> future = new Future<T>();
		Event sentEvent = null;
		synchronized (this) {
			sentEvent = informer.send(event);
			// put future with id as weak ref in pending results table
			pendingRequests.put(sentEvent.getId(), new WeakReference<Future<T>>(future));
			LOG.fine("registered future in pending requests with id: " + sentEvent.getId());
		}
		return future;
	}

	@Override
	public void internalNotify(final Event event) {
		//Thread.dumpStack();
		EventId replyId = null;
		// get reply id
		if (event.getCauses().size() > 0) {
		        try {
			        replyId = event.getCauses().iterator().next();
				LOG.fine("Received reply with id: " + replyId);
			} catch (IllegalArgumentException exception) {
			}
		}
		if (replyId == null) {
		    LOG.warning("Received reply event without cause. Skipping it.");
		    return;
		}

		// check for reply id in list of pending calls
		Future<T> request = null;
		synchronized (this) {
			if (pendingRequests.containsKey(replyId)) {
				request = pendingRequests.get(replyId).get();
				if (request==null) {
					// the future was already garbage collected locally
					LOG.info("Can not deliver reply: Future reference already garbage collected in user code!");
				}
				pendingRequests.remove(replyId);
			}
		}

		// If several clients send requests to the same server
		// method, and EVENT is the reply to one of the
		// "other" requests, REQUEST can be null here.
		if (request!=null) {
			LOG.fine("Found pending reply for id: " + replyId);
			try {
				// an error occured at the server side
				if (event.getMetaData().getUserInfo("rsb:error?")!=null) {
					final String error = (String) event.getData();
					request.error(new RSBException(error));
					return;
				}
			} catch (IllegalArgumentException exception) {
				// ignore
			}
			completeRequest(request,event);
		}
	}

	protected abstract void completeRequest(Future<T> future, Event event);

};
