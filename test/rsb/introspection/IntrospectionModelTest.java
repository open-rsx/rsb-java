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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import rsb.Participant;
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;
import rsb.introspection.IntrospectionModel.IntrospectionModelObserver;

/**
 * @author jwienke
 */
public class IntrospectionModelTest {

    private static class TestIntrospectionModelObserver implements
            IntrospectionModelObserver {

        public static enum CallType {
            ADDED, REMOVED
        }

        public static class Call {

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

        private final List<Call> calls = new LinkedList<Call>();

        @Override
        public void participantAdded(final ParticipantInfo info) {
            final Call call = new Call();
            call.setType(CallType.ADDED);
            call.setInfo(info);
            this.calls.add(call);
        }

        @Override
        public void participantRemoved(final ParticipantInfo info) {
            final Call call = new Call();
            call.setType(CallType.REMOVED);
            call.setInfo(info);
            this.calls.add(call);
        }

        public List<Call> getCalls() {
            return this.calls;
        }

    }

    private static class TestParticipant extends Participant {

        public TestParticipant(final Scope scope) {
            super(scope, new ParticipantConfig());
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

    }

    @Test
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
        assertEquals(2, observer.getCalls().size());
        final TestIntrospectionModelObserver.Call addCall =
                observer.getCalls().get(0);
        assertEquals(TestIntrospectionModelObserver.CallType.ADDED,
                addCall.getType());
        assertEquals(observedParticipant.getId(), addCall.getInfo().getId());
        final TestIntrospectionModelObserver.Call removeCall =
                observer.getCalls().get(1);
        assertEquals(TestIntrospectionModelObserver.CallType.REMOVED,
                removeCall.getType());
        assertEquals(observedParticipant.getId(), removeCall.getInfo().getId());

    }
}
