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

import java.net.URI;
import java.util.Set;

import rsb.Factory.ParticipantObserverManager;
import rsb.config.ParticipantConfig;

/**
 * Base class for all bus participants with an associated scope. Mainly holds
 * references to the router and configuration-level objects.
 *
 * Implementing classes need to ensure that {@link #activate()} and
 * {@link #deactivate()} are called in case these methods are overridden. Method
 * {@link #activate()} needs to be called once all required internal details are
 * set up and processing is possible now and {@link #deactivate()} needs to be
 * called before functionality is teared down.
 *
 * @author jwienke
 * @author swrede
 */
public abstract class Participant extends AbstractActivatable {

    @SuppressWarnings("PMD.ShortVariable")
    private final ParticipantId id = new ParticipantId();
    private final Scope scope;
    private final ParticipantConfig config;
    private ParticipantObserverManager observerManager;
    private ParticipantCreateArgs<?> createArgs;

    /**
     * Creates a new participant on the specified scope.
     *
     * @param args
     *            arguments used to create this participant
     */
    protected Participant(final ParticipantCreateArgs<?> args) {
        if (args.getScope() == null) {
            throw new IllegalArgumentException(
                    "Scope of a participant must not be null.");
        }
        if (args.getConfig() == null) {
            throw new IllegalArgumentException(
                    "ParticipantConfig of a participant must not be null.");
        }
        this.scope = args.getScope();
        this.config = args.getConfig();
        // cache the create args until activate is called to notify observers
        this.createArgs = args;
    }

    @Override
    public void activate() throws RSBException {
        if (this.createArgs == null) {
            throw new IllegalStateException(
                    "Participants can only be activated "
                            + "and subsequently deactivated once. "
                            + "Multiple cycles are not supported.");
        }
        if (this.observerManager != null) {
            this.observerManager.notifyParticipantCreated(this, this.createArgs);
        }
        this.createArgs = null;
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (this.observerManager != null) {
            this.observerManager.notifyParticipantDestroyed(this);
        }
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

    /**
     * Returns the observer manager to notify about participant changes.
     *
     * @return manager or <code>null</code>
     */
    protected ParticipantObserverManager getObserverManager() {
        return this.observerManager;
    }

    /**
     * Sets the observer manager to use for notifying deactivation.
     *
     * Internal use only!
     *
     * @param observerManager
     *            the observer manager to use or <code>null</code> if not
     *            required
     */
    public void setObserverManager(
            final ParticipantObserverManager observerManager) {
        this.observerManager = observerManager;
    }

    /**
     * Returns kind of participant in RSB terminology.
     *
     * @return Key describing participant. One of listener, informer,
     *         local-server, local-method, remote-server, remote-method
     */
    public abstract String getKind();

    /**
     * Returns the data type transfered by this participant.
     *
     * @return type class, <code>null</code> if not applicable for this type of
     *         participant
     */
    public abstract Class<?> getDataType();

    /**
     * Returns URIs describing the transports configured for this participants.
     *
     * Only valid if activated.
     *
     * @return set of transport URIs, not <code>null</code>
     * @throws IllegalStateException
     *             participant is in wrong state to get these URIs
     */
    public abstract Set<URI> getTransportUris();

}
