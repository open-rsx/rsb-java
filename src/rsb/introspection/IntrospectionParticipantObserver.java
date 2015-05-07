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

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.ParticipantObserver;
import rsb.RSBException;

/**
 * Observer instance connecting the creation / deconstruction of participants to
 * the introspection mechanism.
 *
 * @author swrede
 * @author jwienke
 */
public class IntrospectionParticipantObserver implements ParticipantObserver {

    private static final Logger LOG = Logger
            .getLogger(IntrospectionParticipantObserver.class.getName());

    private final IntrospectionModel model;
    private final ProtocolHandler protocol;

    /**
     * Constructs a new instance and accepts a display name to describe the
     * process this instance is operating in.
     *
     * @param processDisplayName
     *            human-readable name of the process this instance operates in,
     *            may be <code>null</code> if not provided
     * @throws rsb.introspection.LacksOsInformationException
     *             thrown in case required information from the operating system
     *             are not available. This makes the introspection unusable.
     */
    public IntrospectionParticipantObserver(final String processDisplayName) {
        this.model = new IntrospectionModel();
        this.protocol = new ProtocolHandler(this.model, processDisplayName);
    }

    @Override
    public void created(final Participant participant,
            final ParticipantCreateArgs<?> args) {

        // only handle participants that want to have introspection enabled
        if (!participant.getConfig().isIntrospectionEnabled()) {
            LOG.log(Level.FINE,
                    "Skipping created processing for participant {0} since"
                            + "introspection is disabled for this participant",
                    participant);
            return;
        }

        synchronized (this.model) {

            // start the introspection protocol with the first new participant
            // so that the internal participants are not active without any need
            if (!this.protocol.isActive()) {

                try {
                    this.protocol.activate();
                    this.model.addObserver(this.protocol);
                } catch (final RSBException e) {
                    LOG.log(Level.SEVERE,
                            "Exception during creation of introspection protocol",
                            e);
                    assert false;
                    // the protocol handler will have reset its state
                    // automatically and can be reused for the next try
                }

            }

        }

        this.model.addParticipant(participant, args.getParent());
    }

    @Override
    public void destroyed(final Participant participant) {

        // only handle participants that want to have introspection enabled
        if (!participant.getConfig().isIntrospectionEnabled()) {
            LOG.log(Level.FINE, "Skipping processing for participant {0} "
                    + "since introspection is disabled for this participant",
                    participant);
            return;
        }

        synchronized (this.model) {

            this.model.removeParticipant(participant);

            // deactivate the introspection participants with the last client
            // participant so that java programs will terminate correctly
            if (this.model.isEmpty() && this.protocol.isActive()) {

                LOG.info("Deactivating introspection protocol handler "
                        + "since no more participants exist");

                this.model.removeObserver(this.protocol);
                try {
                    this.protocol.deactivate();
                } catch (final RSBException e) {
                    LOG.log(Level.SEVERE, "Introspection protocol handler"
                            + "could not be deactivated correctly.", e);
                    // the handler will have reset its state correctly, so we
                    // don't need to do anything
                    assert false;
                } catch (final InterruptedException e) {
                    LOG.log(Level.SEVERE,
                            "Introspection protocol handler could"
                                    + " not be deactivated correctly.", e);
                    // the handler will have reset its state correctly, so we
                    // only need to inform external code about the interrupt
                    // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
                    Thread.currentThread().interrupt();
                }

            }

        }

    }

}
