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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Base class for tests that expose visible markers in the Java logging output
 * between test cases.
 *
 * @author jwienke
 */
public class LoggingTestCase {

    // CHECKSTYLE.OFF: VisibilityModifier - required by junit
    /**
     * Rule to start test start and end logging.
     */
    @Rule
    public final TestNameLoggingRule log = new TestNameLoggingRule();
    // CHECKSTYLE.ON: VisibilityModifier
    //
    private static class TestNameLoggingRule extends TestWatcher {

        private static final Logger LOG = Logger.getLogger("UNITTEST");

        @Override
        protected void failed(final Throwable error, final Description description) {
            LOG.log(Level.INFO, "\n\n----- FAILED: {0}#{1}\n\n", new Object[] {
                    description.getClassName(), description.getMethodName() });
        }

        @Override
        protected void starting(final Description description) {
            LOG.log(Level.INFO,
                    "\n\n----- STARTING: {0}#{1}\n\n",
                    new Object[] { description.getClassName(),
                            description.getMethodName() });
        }

        @Override
        protected void succeeded(final Description description) {
            LOG.log(Level.INFO,
                    "\n\n----- SUCCEEDED: {0}#{1}\n\n",
                    new Object[] { description.getClassName(),
                            description.getMethodName() });
        }

    }

}
