/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import java.util.concurrent.atomic.AtomicInteger;

import rsb.Event;

/**
 * @author swrede
 *
 */
public class ReplyEventCallback implements EventCallback {

	public AtomicInteger counter = new AtomicInteger();	
	
	/* (non-Javadoc)
	 * @see rsb.patterns.EventCallback#invoke(rsb.Event)
	 */
	@Override
	public Event invoke(Event request) throws Throwable {
		Event reply = new Event(String.class);
		reply.setData(request.getData());
		reply.getMetaData().setUserInfo("replyTo", request.getId().getAsUUID().toString());
		counter.incrementAndGet();
		return reply;
	}

}
