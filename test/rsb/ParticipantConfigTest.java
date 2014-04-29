/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import rsb.config.ParticipantConfig;
import rsb.config.TransportConfig;
import rsb.converter.DefaultConverterRepository;
import rsb.eventprocessing.SingleThreadFactory;

/**
 * Test case for {@link ParticipantConfig}.
 *
 * @author jwienke
 */
public class ParticipantConfigTest {

    private ParticipantConfig configA;
    private ParticipantConfig configB;

    @Before
    public void setUp() {
        this.configA = new ParticipantConfig();
        this.configB = new ParticipantConfig();
        // as factories are not easily comparable, use the same instance in both
        // classes
        this.configB.setReceivingStrategy(this.configA.getReceivingStrategy());
    }

    @Test
    public void equalsEmpty() {
        assertEquals(this.configA, this.configB);
    }

    @Test
    public void equalsTransport() {

        final TransportConfig transportA =
                this.configA.getOrCreateTransport("spread");
        assertNotEquals(this.configA, this.configB);

        final TransportConfig transportB =
                this.configB.getOrCreateTransport(transportA.getName());
        assertEquals(this.configA, this.configB);

        final String propertyKey = "foo";
        final String propertyValue = "baar";
        transportA.getOptions().setProperty(propertyKey, propertyValue);
        assertNotEquals(this.configA, this.configB);

        transportB.getOptions().setProperty(propertyKey, propertyValue);
        assertEquals(this.configA, this.configB);

    }

    @Test
    public void equalsStrategy() {

        this.configA.setReceivingStrategy(new SingleThreadFactory());
        assertNotEquals(this.configA, this.configB);

        this.configB.setReceivingStrategy(this.configA.getReceivingStrategy());
        assertEquals(this.configA, this.configB);

    }

    @Test
    public void copy() {

        final ParticipantConfig template = new ParticipantConfig();
        final TransportConfig transport = template.getOrCreateTransport("fooo");
        transport.setEnabled(true);
        transport.setConverters(DefaultConverterRepository
                .getDefaultConverterRepository());
        transport.getOptions().setProperty("blaaa", "blubb");

        assertEquals(template, template.copy());

    }

}
