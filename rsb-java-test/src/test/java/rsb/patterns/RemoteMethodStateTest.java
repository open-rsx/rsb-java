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

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantStateCheck;
import rsb.Scope;
import rsb.config.ParticipantConfig;

/**
 * A {@link ParticipantStateCheck} for {@link LocalMethod} instances.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class RemoteMethodStateTest extends ParticipantStateCheck {

    @Override
    protected Participant createParticipant(
            final ParticipantConfig config) throws Exception {
        return new RemoteMethod(
                new ParticipantCreateArgs<ParticipantCreateArgs<?>>() {
                    // dummy type
                }.setScope(new Scope("/remotemethodtest")).setConfig(config));
    }

}

