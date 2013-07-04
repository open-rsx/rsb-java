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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import rsb.Factory;
import rsb.InitializeException;
import rsb.Scope;

/**
 * @author swrede
 * @author jwienke
 */
public class RemoteServerTest {

    private static final Scope SCOPE = new Scope("/example/test");
    private Factory factory;

    @Before
    public void setUp() {
        this.factory = Factory.getInstance();
    }

    @Test
    public void constructionWithTimeout() {

        final double desiredTimeout = 10;
        final RemoteServer remote = this.factory.createRemoteServer(SCOPE,
                desiredTimeout);
        assertNotNull("RemoteServer construction failed", remote);
        assertEquals("Timeout not resepected upon construction",
                desiredTimeout, remote.getTimeout(), 0.01);
        assertEquals(SCOPE, remote.getScope());
    }

    @Test
    public void constructionWithScope() {
        final RemoteServer remote = this.factory.createRemoteServer(SCOPE);
        assertNotNull("RemoteServer construction failed", remote);
        assertEquals(SCOPE, remote.getScope());
    }

    @Test
    public void constructionWithScopeString() {
        final RemoteServer remote = this.factory.createRemoteServer(SCOPE
                .toString());
        assertNotNull("RemoteServer construction failed", remote);
        assertEquals(SCOPE, remote.getScope());
    }

    @Test
    public void testActivate() throws InitializeException {
        final RemoteServer remote = this.factory.createRemoteServer(SCOPE
                .toString());
        remote.activate();
        remote.deactivate();
    }

    @Test
    public void testAddMethod() throws InitializeException {
        final RemoteServer remote = this.factory.createRemoteServer(SCOPE
                .toString());
        final String methodName = "callme";
        remote.addMethod(methodName);
        remote.activate();
        final Collection<RemoteMethod> methods = remote.getMethods();
        assertEquals(1, methods.size());
        assertEquals(methodName, methods.iterator().next().getName());
        remote.deactivate();
    }

}
