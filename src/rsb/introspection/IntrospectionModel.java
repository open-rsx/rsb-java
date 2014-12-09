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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Participant;
import rsb.ParticipantId;
import rsb.util.OsUtilities;

/**
 * Implementation of RSB-based introspection protocol. Supports hello, bye and
 * survey messages.
 *
 * @author swrede
 * @author ssharma
 */
public class IntrospectionModel {

    private static final Logger LOG = Logger.getLogger(IntrospectionModel.class
            .getName());

    private final List<ParticipantInfo> participants = java.util.Collections
            .synchronizedList(new LinkedList<ParticipantInfo>());
    private final ProcessInfo processInfo;
    private final HostInfo hostInfo;
    private ProtocolHandler protocol;

    @SuppressWarnings("PMD.TooFewBranchesForASwitchStatement")
    public IntrospectionModel() {
        switch (OsUtilities.deriveOsFamily(OsUtilities.getOsName())) {
        case LINUX:
            LOG.fine("Creating Process and CommonHostInfo instances for Linux OS.");
            this.processInfo = new LinuxProcessInfo();
            this.hostInfo = new LinuxHostInfo();
            break;
        default:
            LOG.fine("Creating PortableProcess and PortableHostInfo instances.");
            this.processInfo = new PortableProcessInfo();
            this.hostInfo = new PortableHostInfo();
            break;
        }
    }

    public void setProtocolHandler(final ProtocolHandler protocol) {
        this.protocol = protocol;
    }

    public ProcessInfo getProcessInfo() {
        return this.processInfo;
    }

    public HostInfo getHostInfo() {
        return this.hostInfo;
    }

    public List<ParticipantInfo> getParticipants() {
        return this.participants;
    }

    /**
     * Queries the database of known participants for the participant with the
     * given uuid and returns the associated {@link ParticipantInfo} instance.
     *
     * @param participantId
     *            the UUID of the participant, not <code>null</code>
     * @return the associated participant info or <code>null</code> in case
     *         there is no participant with the given id
     */
    public ParticipantInfo getParticipant(final UUID participantId) {
        assert participantId != null;

        ParticipantInfo participant = null;
        synchronized (this.participants) {
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

    public void addParticipant(final Participant participant,
            final Participant parent) {
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
        this.participants.add(info);
        this.protocol.sendHello(info);
    }

    public void removeParticipant(final Participant participant) {
        LOG.log(Level.FINE, "Removing {0} {1} at {2}", new Object[] {
                participant.getKind().toUpperCase(), participant.getId(),
                participant.getScope() });
        ParticipantInfo info = null;
        synchronized (this.participants) {
            for (final ParticipantInfo participantInfo : this.participants) {
                if (participantInfo.getId() == participant.getId()) {
                    info = participantInfo;
                    this.participants.remove(participantInfo);
                    break;
                }
            }
        }

        if (info == null) {
            LOG.log(Level.FINE, "Trying to remove unknown participant {0}",
                    new Object[] { participant });
            return;
        }

        this.protocol.sendBye(info);

        LOG.log(Level.FINE, "{0} participant(s) remain(s)",
                new Object[] { this.participants.size() });

    }

}
