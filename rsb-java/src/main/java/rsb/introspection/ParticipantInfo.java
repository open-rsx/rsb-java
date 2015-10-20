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

import java.net.URI;
import java.util.Set;

import rsb.ParticipantId;
import rsb.Scope;

/**
 * Internal data holder for participant information. Used within introspection
 * model.
 *
 * @author swrede
 * @author ssharma
 */
public class ParticipantInfo {

    private final String kind;
    @SuppressWarnings("PMD.ShortVariable")
    private final ParticipantId id;
    private final ParticipantId parentId;
    private final Scope scope;
    private final Class<?> type;
    private final Set<URI> transportUris;

    /**
     * Creates a new instance.
     *
     * @param kind
     *            string representation of the type of participant being
     *            represented, not <code>null</code>
     * @param id
     *            id of the participant, not <code>null</code>
     * @param parentId
     *            id of the parent participant or <code>null</code> if there is
     *            no parent
     * @param scope
     *            scope of the participant, not <code>null</code>
     * @param dataType
     *            data type of the participant or <code>null</code> if not
     *            applicable
     * @param transportUris
     *            URIs describing the transports for the represented
     *            participant. Must not be <code>null</code>.
     */
    public ParticipantInfo(final String kind,
            @SuppressWarnings("PMD.ShortVariable") final ParticipantId id,
            final ParticipantId parentId, final Scope scope,
            final Class<?> dataType, final Set<URI> transportUris) {
        assert kind != null;
        assert id != null;
        assert scope != null;
        assert transportUris != null;

        this.kind = kind;
        this.id = id;
        this.parentId = parentId;
        this.scope = scope;
        this.type = dataType;
        this.transportUris = transportUris;
    }

    /**
     * Returns a string representing the type of participant.
     *
     * @return string representation
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Returns the unique ID of the participant.
     *
     * @return participant id
     */
    public ParticipantId getId() {
        return this.id;
    }

    /**
     * Returns the id of the parent participant or <code>null</code> in case
     * there is no parent.
     *
     * @return participant id or <code>null</code>
     */
    public ParticipantId getParentId() {
        return this.parentId;
    }

    /**
     * Returns the scope the participant operates on.
     *
     * @return scope
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Returns the participant's data type or <code>null</code> if a single data
     * type is not applicable for the type of participant.
     *
     * @return data type class or <code>null</code>
     */
    public Class<?> getDataType() {
        return this.type;
    }

    /**
     * Returns the URIs describing the transports the represented participant
     * uses.
     *
     * @return set of URIs, not <code>null</code>
     */
    public Set<URI> getTransportUris() {
        return this.transportUris;
    }

}
