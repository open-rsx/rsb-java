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

import rsb.Factory;
import rsb.config.ParticipantConfig;

/**
 * A JUnit rule to set a specified {@link ParticipantConfig} before a test.
 *
 * @author jwienke
 */
public class ParticipantConfigSetter extends ExternalResource {

    private final ParticipantConfig config;

    /**
     * Creates a new setter that applies the specified config before each test.
     *
     * @param config
     *              the config to apply before each test, not <code>null</code>
     */
    public ParticipantConfigSetter(final ParticipantConfig config) {
        this.config = config;
    }

    @Override
    protected void before() throws Throwable {
        Factory.getInstance().setDefaultParticipantConfig(config);
    }

}
