/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rsb.converter.DefaultConverters;

/**
 * Abstract test for {@link Participant} implementations.
 *
 * @author jwienke
 */
public abstract class ParticipantCheck extends RsbTestCase {

    private Participant participant = null;

    /**
     * Must return an activated participant.
     *
     * @return activated participant, not <code>null</code>
     * @throws Exception
     *             error creating the participant
     */
    protected abstract Participant createParticipant() throws Exception;

    @BeforeClass
    public static void registerConverters() {
        DefaultConverters.register();
    }

    @Before
    public void setUp() throws Exception, Throwable {
        this.participant = createParticipant();
    }

    protected Participant getParticipant() {
        return this.participant;
    }

    @Test
    public void hasTransportUris() {
        final Set<URI> uris = this.participant.getTransportUris();
        assertNotNull(uris);
        assertFalse(uris.isEmpty());
    }

}
