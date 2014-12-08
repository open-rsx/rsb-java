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
import java.util.logging.Logger;

import rsb.Participant;
import rsb.util.OSDetector;

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
    private ProtocolHandler protocol;
    private final HostInfo hostInfo;

    public IntrospectionModel() {
        switch (OSDetector.getOSFamily()) {
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

    public ParticipantInfo getParticipant(final UUID id) {
        ParticipantInfo participant = null;
        synchronized (this.participants) {
            for (final ParticipantInfo it : this.participants) {
                if (it.getId().toString().equals(id.toString())) {
                    participant = it;
                    break;
                }
            }
        }

        if (participant == null) {
            LOG.warning("Couldn't find participant with ID: " + id);
        }
        return participant;
    }

    public void addParticipant(final Participant participant,
            final Participant parent) {
        LOG.fine("Adding " + participant.getKind().toUpperCase() + " "
                + participant.getId() + " at " + participant.getScope()
                + " with parent: " + parent);
        final ParticipantInfo info =
                new ParticipantInfo(participant.getKind(), participant.getId(),
                        (parent != null ? parent.getId() : null),
                        participant.getScope(), participant.getType());
        this.participants.add(info);
        this.protocol.sendHello(info);
    }

    public void removeParticipant(final Participant participant) {
        LOG.fine("Removing " + participant.getKind().toUpperCase() + " "
                + participant.getId() + " at " + participant.getScope());
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

        if (info != null) {
            this.protocol.sendBye(info);
        } else {
            LOG.fine("Trying to remove unknown participant " + participant);
        }

        LOG.fine(this.participants.size() + " participant(s) remain(s)");

    }

}
