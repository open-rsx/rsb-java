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
package rsb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rsb.config.ParticipantConfig;
import rsb.patterns.Reader;

public class FactoryTest extends RsbTestCase {

    private static final Scope SOME_SCOPE = new Scope("/some/scope");

    @Test
    public void createReaderScope() throws Exception {
        final Reader reader = Factory.getInstance().createReader(SOME_SCOPE);
        assertNotNull(reader);
        assertEquals(SOME_SCOPE, reader.getScope());
        assertEquals(Factory.getInstance().getDefaultParticipantConfig(),
                reader.getConfig());
        assertFalse(reader.isActive());
    }

    @Test
    public void createReaderScopeString() throws Exception {
        final Scope scope = new Scope("/a/test");
        final Reader reader = Factory.getInstance().createReader(
                SOME_SCOPE.toString());
        assertNotNull(reader);
        assertEquals(SOME_SCOPE, reader.getScope());
        assertEquals(Factory.getInstance().getDefaultParticipantConfig(),
                reader.getConfig());
        assertFalse(reader.isActive());
    }

    @Test
    public void createReaderScopeConfig() throws Exception {
        final ParticipantConfig config = Factory.getInstance()
            .getDefaultParticipantConfig().copy();
        config.getOrCreateTransport("socket").getOptions().setProperty(
                "foo", "bar");
        final Reader reader = Factory.getInstance().createReader(
                SOME_SCOPE, config);
        assertNotNull(reader);
        assertEquals(SOME_SCOPE, reader.getScope());
        assertEquals(config, reader.getConfig());
        assertNotEquals(Factory.getInstance().getDefaultParticipantConfig(),
                reader.getConfig());
        assertFalse(reader.isActive());
    }

    @Test
    public void createReaderArgs() throws Exception {
        final ReaderCreateArgs args = new ReaderCreateArgs();
        args.setScope(SOME_SCOPE);
        final Reader reader = Factory.getInstance().createReader(args);
        assertNotNull(reader);
        assertEquals(SOME_SCOPE, reader.getScope());
        assertEquals(Factory.getInstance().getDefaultParticipantConfig(),
                reader.getConfig());
        assertFalse(reader.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createReaderArgsNull() throws Exception {
        final ReaderCreateArgs args = null;
        Factory.getInstance().createReader(args);
    }

    @Test
    public void createReaderSetsObserver() throws Exception {
        assertNotNull(Factory.getInstance().createReader(
                    SOME_SCOPE).getObserverManager());
    }

}
