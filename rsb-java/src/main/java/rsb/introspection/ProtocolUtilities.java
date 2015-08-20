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

import rsb.ParticipantId;
import rsb.Scope;

/**
 * Collection of helper methods used to fulfill the introspection protocol.
 *
 * @author jwienke
 */
final class ProtocolUtilities {

    /**
     * Base scope for the whole introspection.
     */
    public static final Scope BASE_SCOPE = new Scope("/__rsb/introspection");

    /**
     * Scope for participant-related communication.
     */
    public static final Scope PARTICIPANT_SCOPE = BASE_SCOPE.concat(new Scope(
            "/participants/"));

    /**
     * Scope for host-related communication.
     */
    public static final Scope HOST_SCOPE = BASE_SCOPE.concat(new Scope(
            "/hosts/"));
    /**
     * Data to send or expect in a ping event.
     */
    public static final String PING_EVENT_DATA = "ping";

    /**
     * Data to send or expect in a pong event.
     */
    public static final String PONG_EVENT_DATA = "pong";

    /**
     * {@link rsb.Event#getMethod()} entry to expect on a request for a specific
     * participant.
     */
    public static final String REQUEST_METHOD_NAME = "REQUEST";

    /**
     * {@link rsb.Event#getMethod()} entry to expect on a participant survey
     * request.
     */
    public static final String SURVEY_METHOD_NAME = "SURVEY";

    /**
     * Name of the RPC server method for echoing back timing information to a
     * caller.
     */
    public static final String ECHO_RPC_METHOD_NAME = "echo";

    /**
     * User timestamp name to echo back the send time.
     */
    public static final String REQUEST_SEND_USER_TIME = "request.send";

    /**
     * User timestamp name to echo back the receive time.
     */
    public static final String REQUEST_RECEIVE_USER_TIME = "request.receive";

    private ProtocolUtilities() {
        // prevent utility class instantiation
    }

    /**
     * Tests whether the scope formally can be interpreted as a scope for for a
     * specific participant.
     *
     * @param scope
     *            the scope to test
     * @return <code>true</code> if the scope has sufficient components to be
     *         the designator for a specific participant
     */
    public static boolean isSpecificParticipantScope(final Scope scope) {
        return scope != null
                && scope.isSubScopeOf(PARTICIPANT_SCOPE)
                && scope.getComponents().size() == PARTICIPANT_SCOPE
                        .getComponents().size() + 1;
    }

    /**
     * Creates a scope to send data messages on that regard a specific
     * participant.
     *
     * @param participant
     *            the participant to create the scope for
     * @return a scope related to the participant, subscope of
     *         {@link #PARTICIPANT_SCOPE}.
     */
    public static Scope participantScope(final ParticipantInfo participant) {
        assert participant != null;
        return PARTICIPANT_SCOPE.concat(new Scope(Scope.COMPONENT_SEPARATOR
                + participant.getId()));
    }

    /**
     * Extracts a {@link ParticipantId} from a scope designating a specific
     * participant.
     *
     * @param scope
     *            the scope to parse
     * @return the parsed {@link ParticipantId}
     * @throws IllegalArgumentException
     *             scope does not contain a valid id
     */
    public static ParticipantId participantIdFromScope(final Scope scope) {
        if (!isSpecificParticipantScope(scope)) {
            throw new IllegalArgumentException(String.format(
                    "Scope %s is not a scope "
                            + "that can contain a participant id", scope));
        }
        // we expect the last scope component to be the id of the
        // participant this request is targeted at
        final String idString =
                scope.getComponents().get(scope.getComponents().size() - 1);
        return new ParticipantId(idString);
    }

}
