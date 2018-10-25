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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rsb.converter.DefaultConverters;
import rsb.transport.DefaultTransports;

//CHECKSTYLE.OFF: JavadocMethod - test class

/**
 * Abstract test case for the different states of participants.
 *
 * @author jwienke
 */
public abstract class ParticipantStateCheck extends RsbTestCase {

    private Participant participant = null;

    protected abstract Participant createParticipant() throws Exception;

    @BeforeClass
    public static void registerConverters() {
        DefaultConverters.register();
        DefaultTransports.register();
    }

    @Before
    public void setUp() throws Exception {
        this.participant = createParticipant();
    }

    @After
    public void tearDown() {
        if (this.participant != null) {
            try {
                this.participant.deactivate();
            } catch (final Exception e) {
                // ignore, we just try to rescue things
            }
        }
    }

    @Test
    public void inactiveInTheBeginning() throws Exception {
        assertFalse(this.participant.isActive());
    }

    @Test
    public void activeAfterActivation() throws Exception {
        this.participant.activate();
        assertTrue(this.participant.isActive());
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionOnDuplicatedActivation() throws Exception {
        this.participant.activate();
        this.participant.activate();
    }

    @Test
    public void deactiveAfterDeactivation() throws Exception {
        this.participant.activate();
        this.participant.deactivate();
        assertFalse(this.participant.isActive());
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionOnDuplicatedDeactivation() throws Exception {
        this.participant.activate();
        this.participant.deactivate();
        this.participant.deactivate();
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionOnReactivation() throws Exception {
        this.participant.activate();
        this.participant.deactivate();
        this.participant.activate();
    }

}

//CHECKSTYLE.ON: JavadocMethod
