/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

import static org.junit.Assert.fail;

import org.junit.Test;

import rsb.Participant;
import rsb.ParticipantStateCheck;
import rsb.RemoteServerCreateArgs;
import rsb.Scope;
import rsb.Utilities;
import rsb.config.ParticipantConfig;

/**
 * A {@link ParticipantStateCheck} for {@link RemoteServer} instances.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class RemoteServerStateTest extends ParticipantStateCheck {

    @Override
    protected Participant createParticipant(
            final ParticipantConfig config) throws Exception {
        return new RemoteServer(new RemoteServerCreateArgs().setScope(
                new Scope("/some/scope")).setConfig(config));
    }

    /**
     * Checks that deactivation is successful even after a failed call to a
     * previously uncalled method.
     *
     * @throws Exception
     *             test error
     */
    @Test
    public void deactivationAfterNewMethodFailure() throws Exception {
        final RemoteServer server = (RemoteServer) createParticipant(
                Utilities.createBrokenParticipantConfig());
        server.activate();
        try {
            server.call("failingmethod");
            // The previous call must fail as it is the first one to create
            // actual participants that interact with the transport.
            fail("Calling a method with a broken transport configuration "
                    + "must fail");
            // We cannot use the junit annotation for expected exceptions here
            // as also deactivate can raise the same exception type.
        } catch (final Exception e) {
            // expected here
        }
        // this must still succeed
        server.deactivate();
    }

}
