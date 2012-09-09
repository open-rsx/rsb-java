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

import static org.junit.Assert.*;

import java.util.logging.Logger;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

import org.junit.Test;

import rsb.patterns.Future;

/**
 * @author jmoringe
 *
 */
public class FutureTest {

    final static private Logger LOG = Logger.getLogger(FutureTest.class.getName());

    @Test
    public void testSimpleGet() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.complete(1);
	assertTrue(future.get() == 1);
    }

    @Test(expected=CancellationException.class)
    public void testCancel() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.cancel(true);
	future.get();
    }

    @Test(expected=TimeoutException.class)
    public void testTimeout() throws ExecutionException, TimeoutException {
	Future<Integer> future = new Future();
	future.get(10);
    }



}
