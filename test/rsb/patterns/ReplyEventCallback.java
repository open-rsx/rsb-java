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

import java.util.concurrent.atomic.AtomicInteger;

import rsb.Event;

/**
 * @author swrede
 * 
 */
public class ReplyEventCallback extends EventCallback {

    public AtomicInteger counter = new AtomicInteger();

    @Override
    public Event invoke(final Event request) throws Throwable {
        final Event reply = new Event(String.class, request.getData());
        reply.getMetaData().setUserInfo("replyTo",
                request.getId().getAsUUID().toString());
        this.counter.incrementAndGet();
        return reply;
    }

}
