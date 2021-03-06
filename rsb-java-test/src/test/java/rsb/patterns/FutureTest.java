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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import rsb.RsbTestCase;

/**
 * @author jmoringe
 */
public class FutureTest extends RsbTestCase {

    @Test
    public void simpleGet() throws Exception {
        final Future<Integer> future = new Future<Integer>();
        future.complete(1);
        assertEquals(Integer.valueOf(1), future.get());
    }

    @Test(expected = CancellationException.class)
    public void cancel() throws Exception {
        final Future<Integer> future = new Future<Integer>();
        future.cancel(true);
        future.get();
    }

    @Test(expected = TimeoutException.class)
    public void timeout() throws Exception {
        final Future<Integer> future = new Future<Integer>();
        final int shortTimeout = 10;
        future.get(shortTimeout);
    }

    @Test(timeout = 4000)
    public void interruption() throws Exception {
        final Future<Integer> future = new Future<Integer>();
        final Boolean[] error = new Boolean[] { false };
        final Thread waiter = new Thread() {

            @Override
            public void run() {
                try {
                    future.get();
                } catch (final ExecutionException e) {
                    error[0] = true;
                } catch (final InterruptedException e) {
                    // expected and wanted
                }
            }
        };
        waiter.start();
        waiter.interrupt();
        waiter.join();
        assertFalse(error[0]);
    }
}
