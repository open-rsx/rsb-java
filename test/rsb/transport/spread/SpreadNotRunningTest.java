/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.transport.spread;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.patterns.DataCallback;
import rsb.patterns.LocalServer;

/**
 * Test for situations where no connection to a Spread daemon is available.
 *
 * Pre-Condition: Spread is not running
 *
 * @author swrede
 */
public class SpreadNotRunningTest {

    @BeforeClass
    public static void prepare() {
        // TODO restore this!
        // final Properties prop = Properties.getInstance();
        // // prop.setProperty("transport.socket.enabled", "false");
        // prop.setProperty("transport.spread.enabled", "true");
        // prop.setProperty("transport.spread.retry", "1");
    }

    @Ignore
    @Test(expected = InitializeException.class)
    public void informer() throws InitializeException {
        final Factory factory = Factory.getInstance();
        final Informer<Object> inf = factory.createInformer("/foo");
        inf.activate();
    }

    @Ignore
    @Test(expected = InitializeException.class)
    public void server() throws Throwable {
        final Factory factory = Factory.getInstance();
        final LocalServer server = factory.createLocalServer("/bar");
        server.addMethod("foobar", new DataCallback<String, String>() {

            @Override
            public String invoke(final String request) throws Throwable {
                return "foobar";
            }
        });
        server.activate();
    }

}
