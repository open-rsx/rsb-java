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
package rsb;

import org.junit.Rule;

import rsb.testutils.LoggingTestCase;
import rsb.testutils.ParticipantConfigSetter;
import rsb.testutils.SleepRule;

/**
 * Derive all test cases from this class to improve the logging output by
 * including the currently running test name.
 *
 * @author jwienke
 */

public class RsbTestCase extends LoggingTestCase {

    // CHECKSTYLE.OFF: VisibilityModifier - required by junit
    /**
     * Rule to set a default participant config.
     */
    @Rule
    public final ParticipantConfigSetter setter = new ParticipantConfigSetter(
            Utilities.createParticipantConfig());

    /**
     * Rule to sleep between tests if necessary.
     */
    @Rule
    public final SleepRule sleeper = new SleepRule();
    // CHECKSTYLE.ON: VisibilityModifier

}
