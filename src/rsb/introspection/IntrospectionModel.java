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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Participant;
import rsb.ParticipantId;

/**
 * A model of existing participants in an RSB-based system. The model is a
 * collection of participant representations and can be observed. The
 * implementation is thread-safe. In case multiple operations need to be atomic,
 * clients can synchronize on this class.
 *
 * @author swrede
 * @author ssharma
 * @author jwienke
 */
public class IntrospectionModel {

    /**
     * Interface to implement when observing an instance of
     * {@link IntrospectionModel}. Clients will only be called single-threaded.
     *
     * @author jwienke
     */
    public interface IntrospectionModelObserver {

        /**
         * Called in case a new participant has been added to the model.
         *
         * @param info
         *            information about the participant, not <code>null</code>
         */
        void participantAdded(final ParticipantInfo info);

        /**
         * Called in case a participant has been removed from the model.
         *
         * @param info
         *            information about the participant, not <code>null</code>
         */
        void participantRemoved(final ParticipantInfo info);

    }

    private static final Logger LOG = Logger.getLogger(IntrospectionModel.class
            .getName());

    private final List<ParticipantInfo> participants =
            new LinkedList<ParticipantInfo>();

    private final Set<IntrospectionModelObserver> observers =
            new HashSet<IntrospectionModelObserver>();

    /**
     * Adds an observer to this model.
     *
     * @param observer
     *            observer to add, not <code>null</code>
     * @return <code>true</code> if the oberserver was newly added
     */
    public boolean addObserver(final IntrospectionModelObserver observer) {
        assert observer != null;
        synchronized (this) {
            return this.observers.add(observer);
        }
    }

    /**
     * Removes an observer from this model.
     *
     * @param observer
     *            observer to remove, not <code>null</code>
     * @return <code>true</code> if the observer existed and was removed
     */
    public boolean removeObserver(final IntrospectionModelObserver observer) {
        synchronized (this) {
            return this.observers.remove(observer);
        }
    }

    /**
     * Returns the raw collection of participants. Clients need to ensure
     * synchronization on this instance when using the returned collection.
     *
     * @return list of participants
     */
    public List<ParticipantInfo> getParticipants() {
        return this.participants;
    }

    /**
     * Indicates whether any participants are known.
     *
     * @return <code>true</code> if the model contains participants
     */
    public boolean isEmpty() {
        return this.participants.isEmpty();
    }

    /**
     * Queries the database of known participants for the participant with the
     * given uuid and returns the associated {@link ParticipantInfo} instance.
     * Remember to synchronize on this instance in case you want to ensure that
     * the participant still exists in the model when you operate with it.
     *
     * @param participantId
     *            the UUID of the participant, not <code>null</code>
     * @return the associated participant info or <code>null</code> in case
     *         there is no participant with the given id
     */
    public ParticipantInfo getParticipant(final UUID participantId) {
        assert participantId != null;

        ParticipantInfo participant = null;
        synchronized (this) {
            for (final ParticipantInfo info : this.participants) {
                if (info.getId().toString().equals(participantId.toString())) {
                    participant = info;
                    break;
                }
            }
        }

        if (participant == null) {
            LOG.log(Level.WARNING, "Couldn't find participant with ID: {0}",
                    new Object[] { participantId });
        }

        return participant;

    }

    /**
     * Adds a participant to the model.
     *
     * @param participant
     *            the new participant, not <code>null</code>
     * @param parent
     *            optionally the parent participant or <code>null</code> if no
     *            parent exists
     */
    public void addParticipant(final Participant participant,
            final Participant parent) {
        assert participant != null;
        LOG.log(Level.FINE,
                "Adding {0} {1} at {2} with parent {2}",
                new Object[] { participant.getKind().toUpperCase(),
                        participant.getId(), participant.getScope(), parent });

        ParticipantId parentId = null;
        if (parent != null) {
            parentId = parent.getId();
        }
        final ParticipantInfo info =
                new ParticipantInfo(participant.getKind(), participant.getId(),
                        parentId, participant.getScope(),
                        participant.getDataType());

        synchronized (this) {

            this.participants.add(info);

            synchronized (this.observers) {
                // use a copy of the observers set so that clients can register
                // or unregister during notification
                final HashSet<IntrospectionModelObserver> observersCopy =
                        new HashSet<IntrospectionModelObserver>(this.observers);
                for (final IntrospectionModelObserver observer : observersCopy) {
                    observer.participantAdded(info);
                }
            }

        }

    }

    /**
     * Removes a participant from the model.
     *
     * @param participant
     *            participant to remove, not <code>null</code>
     */
    public void removeParticipant(final Participant participant) {
        assert participant != null;
        LOG.log(Level.FINE, "Removing {0} {1} at {2}", new Object[] {
                participant.getKind().toUpperCase(), participant.getId(),
                participant.getScope() });

        synchronized (this) {

            ParticipantInfo info = null;
            for (final ParticipantInfo participantInfo : this.participants) {
                if (participantInfo.getId() == participant.getId()) {
                    info = participantInfo;
                    this.participants.remove(participantInfo);
                    break;
                }
            }

            if (info == null) {
                LOG.log(Level.FINE, "Trying to remove unknown participant {0}",
                        new Object[] { participant });
                return;
            }

            synchronized (this.observers) {
                // use a copy of the observers set so that clients can register
                // or unregister during notification
                final HashSet<IntrospectionModelObserver> observersCopy =
                        new HashSet<IntrospectionModelObserver>(this.observers);
                for (final IntrospectionModelObserver observer : observersCopy) {
                    observer.participantRemoved(info);
                }
            }

            LOG.log(Level.FINE, "{0} participant(s) remain(s)",
                    new Object[] { this.participants.size() });

        }

    }

}
