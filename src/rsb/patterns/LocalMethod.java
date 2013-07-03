/**
 * ============================================================
 *
 * This file is part of the rsb-java project
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

import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.RSBException;
import rsb.filter.MethodFilter;

/**
 * Objects of this class implement and make available methods of a
 * local server.
 *
 * The actual behavior of methods is implemented by invoking arbitrary
 * user-supplied functions.
 *
 * @author jmoringe
 * @author swrede
 *
 * @param <T>	return type of supplied handler method
 * @param <U>	parameter object type of supplied handler method
 *
 */
class LocalMethod<T, U> extends Method implements Handler  {

	private final static Logger LOG = Logger.getLogger(LocalMethod.class.getName());

	DataCallback<T, U> callback = null;
	EventCallback eventCallback = null;

	/**
	 * Create a new LocalMethod object that is exposed under the name @a name by @a
	 * server.
	 *
	 * @param server
	 *            The local server object to which the method will be
	 *            associated.
	 * @param name
	 *            The name of the method.
	 */
	public LocalMethod(Server server, String name, DataCallback<T, U> callback) {
		super(server, name);
		listener = factory.createListener(REQUEST_SCOPE);
		informer = factory.createInformer(REPLY_SCOPE);
		this.callback = callback;
		listener.addFilter(new MethodFilter("REQUEST"));
		listener.addHandler(this, true);
	}

	public LocalMethod(Server server, String name, EventCallback callback) {
		super(server, name);
		listener = factory.createListener(REQUEST_SCOPE);
		informer = factory.createInformer(REPLY_SCOPE);
		this.eventCallback = callback;
		listener.addFilter(new MethodFilter("REQUEST"));
		listener.addHandler(this, true);
	}

	@Override
	public void internalNotify(Event event) {
		// return reply as direct event, hence set some metadata here
		Event reply = new Event();

		// invoke correct callback
		try {
			if (callback!=null) {
				invokeDataCallback(event, reply);
			} else {
				reply = eventCallback.invoke(event);
			}
		} catch (Throwable exception) {
			LOG.warning("Exception during method invocation in participant: " + REQUEST_SCOPE + " Exception message: " + exception);
			final String error = exception.toString() + " Details: " + exception.getMessage();
			// return error information
			reply.setType(String.class);
			reply.getMetaData().setUserInfo("rsb:error?", "1");
			reply.setData(error);
		}

		reply.setScope(REPLY_SCOPE);
		reply.setMethod("REPLY");
		reply.addCause(event.getId());

		// send reply via method informer
		try {
			getInformer().send(reply);
		} catch (RSBException exception) {
			// TODO call local error handler
			LOG.severe("Exception while sending reply in server method: " + REPLY_SCOPE + " Exception message: " + exception.getMessage());
		}
	}

	/**
	 * @param event
	 * @param reply
	 * @throws Throwable
	 */
	private void invokeDataCallback(Event event, Event reply) throws Throwable {
		@SuppressWarnings("unchecked")
		T result = callback.invoke((U) event.getData());
		// return result of invocation
		if (result != null) {
		    reply.setType(result.getClass());
		} else {
		    reply.setType(Void.class);
		}
		reply.setData(result);
	}
};
