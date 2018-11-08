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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import rsb.RsbTestCase;
import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;
import rsb.introspection.IntrospectionModel.IntrospectionModelObserver;

/**
 * @author jwienke
 */
public class IntrospectionModelTest extends RsbTestCase {

    private static class TestIntrospectionModelObserver implements
            IntrospectionModelObserver {

        private final List<ObserverCall> calls = new LinkedList<ObserverCall>();

        public enum CallType {
            ADDED, REMOVED
        }

        public static class ObserverCall {

            private CallType type;
            private ParticipantInfo info;

            public CallType getType() {
                return this.type;
            }

            public void setType(final CallType type) {
                this.type = type;
            }

            public ParticipantInfo getInfo() {
                return this.info;
            }

            public void setInfo(final ParticipantInfo info) {
                this.info = info;
            }

        }

        @Override
        public void participantAdded(final ParticipantInfo info) {
            final ObserverCall call = new ObserverCall();
            call.setType(CallType.ADDED);
            call.setInfo(info);
            this.calls.add(call);
        }

        @Override
        public void participantRemoved(final ParticipantInfo info) {
            final ObserverCall call = new ObserverCall();
            call.setType(CallType.REMOVED);
            call.setInfo(info);
            this.calls.add(call);
        }

        public List<ObserverCall> getObserverCalls() {
            return this.calls;
        }

    }

    private static class TestParticipant extends Participant {

        TestParticipant(final Scope scope) {
            super(new ParticipantCreateArgs<ParticipantCreateArgs<?>>() {
                // dummy type
            }.setScope(scope).setConfig(new ParticipantConfig()));
        }

        @Override
        public void activate() throws RSBException {
            // do nothing here
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String getKind() {
            return "TestParticipant";
        }

        @Override
        public Class<?> getDataType() {
            return Object.class;
        }

        @Override
        public Set<URI> getTransportUris() {
            return new HashSet<URI>();
        }

    }

    // hard to express this in multiple tests without a lot of repetition
    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void observer() {

        final IntrospectionModel model = new IntrospectionModel();

        final Participant unobservedParticipant =
                new TestParticipant(new Scope("/blubb"));
        model.addParticipant(unobservedParticipant, null);

        final TestIntrospectionModelObserver observer =
                new TestIntrospectionModelObserver();
        assertTrue(model.addObserver(observer));
        assertFalse(model.addObserver(observer));

        final Participant observedParticipant =
                new TestParticipant(new Scope("/yes"));
        model.addParticipant(observedParticipant, null);
        model.removeParticipant(observedParticipant);

        assertTrue(model.removeObserver(observer));
        assertFalse(model.removeObserver(observer));

        model.removeParticipant(unobservedParticipant);

        // ensure expected call sequence
        assertEquals(2, observer.getObserverCalls().size());
        final TestIntrospectionModelObserver.ObserverCall addCall =
                observer.getObserverCalls().get(0);
        assertEquals(TestIntrospectionModelObserver.CallType.ADDED,
                addCall.getType());
        assertEquals(observedParticipant.getId(), addCall.getInfo().getId());
        final TestIntrospectionModelObserver.ObserverCall removeCall =
                observer.getObserverCalls().get(1);
        assertEquals(TestIntrospectionModelObserver.CallType.REMOVED,
                removeCall.getType());
        assertEquals(observedParticipant.getId(), removeCall.getInfo().getId());

    }

    // hard to express this in multiple tests without a lot of repetition
    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void addRemove() {
        final IntrospectionModel model = new IntrospectionModel();
        assertTrue(model.isEmpty());

        final Participant participant = new TestParticipant(new Scope("/bla"));
        model.addParticipant(participant, null);
        assertFalse(model.isEmpty());
        assertEquals(1, model.getParticipants().size());
        assertEquals(participant.getId(), model.getParticipants().iterator()
                .next().getId());

        model.addParticipant(participant, null);
        assertEquals("Adding the same participant twice "
                + "must be handled by the model.", 1, model.getParticipants()
                .size());

        model.removeParticipant(new TestParticipant(new Scope("/blbbbasdasd")));
        assertEquals("Removing a participant that never was in the model "
                + "must not change the model.", 1, model.getParticipants()
                .size());

        model.removeParticipant(participant);
        assertTrue(model.isEmpty());

    }

}
