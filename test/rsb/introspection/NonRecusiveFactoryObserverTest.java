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
import rsb.InitializeException;
import rsb.Listener;
import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;
import rsb.RSBException;
import rsb.Scope;

/**
 * Development test case for preventing recursive calls to a participant
 * observer. The test case should fail with StackOverflowException if the
 * condition to prevent recursive calls is not correct.
 *
 * @author swrede
 */
public class NonRecusiveFactoryObserverTest {

    class RecursiveFactoryObserver implements ParticipantObserver {

        @Override
        public void created(final Participant participant,
                final ParticipantCreateArgs<?> args) {
            if (participant.getScope().isSubScopeOf(new Scope("/nonrecursive"))) {
                final Factory factory = Factory.getInstance();
                try {
                    factory.createInformer("/recursive/a");
                } catch (final InitializeException e) {
                    e.printStackTrace();
                }
            } else if (participant.getScope().isSubScopeOf(
                    new Scope("/recursive"))) {
                System.out
                        .println("Not creating informer due to recursive notification.");
            }
        }

        @Override
        public void destroyed(final Participant participant) {
            // intentionally left blank

        }
    }

    @Test
    public void test() throws RSBException, InterruptedException {
        final Factory factory = Factory.getInstance();
        factory.addObserver(new LoggingObserver());
        factory.addObserver(new RecursiveFactoryObserver());

        final Listener listener = factory.createListener("/nonrecursive/b");
        listener.activate();
        listener.deactivate();
    }

}
