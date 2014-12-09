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

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Activatable;
import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;
import rsb.RSBException;

/**
 * Observer instance connecting the creation / deconstruction of participants to
 * the introspection mechanism.
 *
 * @author swrede
 */
public class IntrospectionParticipantObserver implements ParticipantObserver,
        Activatable {

    private static final Logger LOG = Logger
            .getLogger(IntrospectionParticipantObserver.class.getName());

    private final IntrospectionModel model;
    private ProtocolHandler protocol;

    /**
     * Creates a new instance with containing new instances of
     * {@link IntrospectionModel} and {@link ProtocolHandler}.
     */
    public IntrospectionParticipantObserver() {
        this.model = new IntrospectionModel();
        this.protocol = new ProtocolHandler(this.model);
    }

    /**
     * Creates a new instance with the specified introspection backend and
     * handler.
     *
     * @param model
     *            the model to use, not <code>null</code>
     * @param protocol
     *            the protocol handler to use, not <code>null</code>
     */
    public IntrospectionParticipantObserver(final IntrospectionModel model,
            final ProtocolHandler protocol) {
        assert model != null;
        assert protocol != null;
        this.model = model;
        this.protocol = protocol;
    }

    @Override
    public void activate() throws RSBException {
        assert this.protocol != null;
        this.protocol.activate();
        LOG.fine("activated");
    }

    @Override
    public void deactivate() {
        if (!this.protocol.isActive()) {
            return;
        }

        try {
            this.protocol.deactivate();
        } catch (final RSBException e) {
            LOG.log(Level.WARNING,
                    "RSBException upon deactivation of introspection protocol",
                    e);
        } catch (final InterruptedException e) {
            LOG.log(Level.WARNING, "InterruptedException upon deactivation "
                    + "of introspection protocol", e);
        }
    }

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {
        if (participant.getScope().isSubScopeOf(ProtocolHandler.BASE_SCOPE)) {
            return;
        }
        synchronized (this) {
            if (!this.protocol.isActive()) {
                // lazy instantiation of protocol handler due to otherwise
                // recursive factory calls
                try {
                    this.protocol.activate();
                    this.model.addObserver(this.protocol);
                } catch (final RSBException e) {
                    LOG.log(Level.WARNING,
                            "Exception during creation of introspection protocol",
                            e);
                    // if introspection participants cannot be created, we
                    // probably have a serious problem anyway
                    this.model.removeObserver(this.protocol);
                    this.protocol = new ProtocolHandler(this.model);
                    this.model.addObserver(this.protocol);
                    assert false;
                }
            }
        }

        this.model.addParticipant(participant, args.getParent());
    }

    @Override
    public void destroyed(final Participant participant) {
        synchronized (this) {
            if ((this.model != null)
                    && (!participant.getScope().isSubScopeOf(
                            ProtocolHandler.BASE_SCOPE))) {
                this.model.removeParticipant(participant);
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.protocol.isActive();
    }

}
