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
package rsb.introspection;

import org.junit.Test;

import rsb.Factory;
import rsb.Informer;
import rsb.Listener;
import rsb.RSBException;

/**
 * @author swrede
 */
public class ManualIntrospectionTest {

    @Test
    public void simpleNonTest() throws RSBException, InterruptedException {
        final Factory factory = Factory.getInstance();

        if (!Factory.getInstance().getProperties()
                .getProperty("rsb.introspection", "false").asBoolean()) {
            Factory.getInstance().addObserver(
                    new IntrospectionParticipantObserver());
        }

        // regular RSB API usage, example here: listener creation and
        // destruction
        final Listener listener = factory.createListener("/rsbtest");
        listener.activate();
        final Informer<String> informer =
                factory.createInformer("/rsbtest", String.class);
        informer.activate();
        Thread.sleep(1000);
        listener.deactivate();
        informer.deactivate();
    }

}
