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
package rsb;

import rsb.config.ParticipantConfig;

/**
 * Base class for all bus participants with an associated scope. Mainly holds
 * references to the router and configuration-level objects.
 *
 * @author jwienke
 * @author swrede
 */
public abstract class Participant implements RSBObject {

    @SuppressWarnings("PMD.ShortVariable")
    private final ParticipantId id = new ParticipantId();
    private final Scope scope;
    private final ParticipantConfig config;

    /**
     * Creates a new participant on the specified scope.
     *
     * @param scope
     *            scope of the participant
     * @param config
     *            configuration of the participant
     */
    protected Participant(final Scope scope, final ParticipantConfig config) {
        if (scope == null) {
            throw new IllegalArgumentException(
                    "Scope of a participant must not be null.");
        }
        if (config == null) {
            throw new IllegalArgumentException(
                    "ParticipantConfig of a participant must not be null.");
        }
        this.scope = scope;
        this.config = config;
    }

    /**
     * Creates a new participant on the specified scope.
     *
     * @param scope
     *            scope of the participant
     * @param config
     *            configuration of the participant
     */
    protected Participant(final String scope, final ParticipantConfig config) {
        this(new Scope(scope), config);
    }

    /**
     * Returns the unique ID of this participant.
     *
     * @return the unique id of the participant
     */
    public ParticipantId getId() {
        return this.id;
    }

    /**
     * Returns the scope of this participant.
     *
     * @return scope of the participant, not <code>null</code>
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Returns the {@link ParticipantConfig} used for this participant.
     *
     * @return instance not <code>null</code>
     */
    public ParticipantConfig getConfig() {
        return this.config;
    }

}
