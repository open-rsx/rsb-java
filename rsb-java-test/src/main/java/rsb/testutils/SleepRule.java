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
package rsb.testutils;

import org.junit.rules.ExternalResource;

/**
 * A JUnit test rule to sleep between tests. Sleeping is activated if the
 * property rsb.test.sleep is set to <code>true</code>.
 *
 * @author jwienke
 */
public class SleepRule extends ExternalResource {

    private static final long SLEEP_TIME_MS = 700;

    @Override
    protected void after() {
        // if requested via system property, sleep a bit after each test to
        // mitigate https://issues.apache.org/jira/browse/SUREFIRE-1587
        if (Boolean.getBoolean("rsb.test.sleep")) {
            try {
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
