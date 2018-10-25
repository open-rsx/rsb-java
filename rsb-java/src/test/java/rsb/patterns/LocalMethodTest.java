/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2018 CoR-Lab, Bielefeld University
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

import org.junit.Test;

import rsb.Event;
import rsb.EventId;
import rsb.Factory;
import rsb.RsbTestCase;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author jwienke
 */
public class LocalMethodTest extends RsbTestCase {

    @Test
    public void testHandleNullReply() throws Exception {

        final Callback callback = new Callback() {

            @Override
            public Event internalInvoke(final Event request)
                    throws UserCodeException {
                return null;
            }

        };

        final LocalMethod method = new LocalMethod(
                new ParticipantCreateArgs<ParticipantCreateArgs<?>>() {
                    // dummy type
                }.setScope(new Scope("/test")).setConfig(
                    Factory.getInstance().getDefaultParticipantConfig()),
                callback);
        method.activate();

        try {
            final Event dummyRequest = new Event();
            dummyRequest.setId(new EventId(new ParticipantId(), 0));
            // must not throw an exception despite the callback returning null
            method.internalNotify(dummyRequest);
        } finally {
            method.deactivate();
        }
    }

}
