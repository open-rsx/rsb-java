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

import rsb.transport.PortConfiguration;
import rsb.transport.Router;
import rsb.transport.TransportFactory;

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
    private Scope scope;
    private TransportFactory transportFactory;
    private Router router;

    /**
     * Creates a new participant on the specified scope.
     *
     * @param scope
     *            scope of the participant
     * @param transportFactory
     *            the factory used for transports for this {@link Participant}
     * @param portConfig
     *            type of ports to create for this participant
     */
    protected Participant(final Scope scope,
            final TransportFactory transportFactory,
            final PortConfiguration portConfig) {
        this.initMembers(scope, transportFactory, portConfig);
    }

    /**
     * Creates a new participant on the specified scope.
     *
     * @param scope
     *            scope of the participant
     * @param transportFactory
     *            the factory used for transports for this {@link Participant}
     * @param portConfig
     *            type of ports to create for this participant
     */
    protected Participant(final String scope,
            final TransportFactory transportFactory,
            final PortConfiguration portConfig) {
        this.initMembers(new Scope(scope), transportFactory, portConfig);
    }

    /**
     * @param scope
     * @param transportFactory
     * @param portConfig
     */
    private void initMembers(final Scope scope,
            final TransportFactory transportFactory,
            final PortConfiguration portConfig) {
        if (scope == null) {
            throw new IllegalArgumentException(
                    "Scope of a participant must not be null.");
        }
        if (transportFactory == null) {
            throw new IllegalArgumentException(
                    "TransportFactory of a participant must not be null.");
        }
        this.scope = scope;
        this.transportFactory = transportFactory;
        this.router = new Router(this.transportFactory, portConfig, this.scope);
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
     * Returns the {@link TransportFactory} used for this participant.
     *
     * @return instance not <code>null</code>
     */
    protected TransportFactory getTransportFactory() {
        return this.transportFactory;
    }

    /**
     * Returns the router used for this participant.
     *
     * @return router used for this participant, not <code>null</code>
     */
    protected Router getRouter() {
        return this.router;
    }

}
